package databaselogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;

import domain.LiteratureGroupManager;
import domain.LiteratureGrouping;
import domain.LiteratureProduct;
import domain.LiteratureProductManager;
import domain.LiteratureReview;
import domain.LiteratureReviewManager;
import domain.Review;

public class DerbyDBPersistance {
	public static int GROUPS=0;
	public static int LITREVS=1;
	public static int PRODUCTS=2;
	public static int REVIEWS=3;
	
	/* the default framework is embedded*/
    //private String framework = "embedded";
    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String protocol = "jdbc:derby:";
    
    private String litToolDB = "LitToolDataBase";
    private Connection conn;
    private PreparedStatement groupInsert = null;
    private PreparedStatement litrevInsert = null;
    private PreparedStatement reviewInsert = null;
    private PreparedStatement productInsert = null;
    private PreparedStatement productLinkInsert = null;
    
    private PreparedStatement groupUpdate = null;
    private PreparedStatement litrevUpdate = null;
    private PreparedStatement reviewUpdate = null;
    private PreparedStatement productUpdate = null;
    private PreparedStatement productLinkUpdate = null;
    
    private PreparedStatement groupRemove = null;
    private PreparedStatement litrevRemove = null;
    private PreparedStatement reviewRemove = null;
    private PreparedStatement productRemove = null;
    private PreparedStatement productLinkRemove = null;
    
    private LinkedList<Statement> sqlStatements = new LinkedList<Statement>();
    
	private static DerbyDBPersistance derby = new DerbyDBPersistance();
	
	private DerbyDBPersistance(){};
	
	public static DerbyDBPersistance getInstance(){return derby;};
	
	public void addLink(String parentName,String childName){
		DBLogger.getInstance().print("Derby", "Parent "+parentName+" child "+childName);
		try{
			productLinkInsert.setInt(1, (parentName.hashCode()+childName.hashCode()) );
			productLinkInsert.setInt(2, parentName.hashCode());
			productLinkInsert.setInt(3, childName.hashCode());
			productLinkInsert.executeUpdate();
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "Error adding links "+e);
			e.printStackTrace();
		}
	}
	
	public void unLink(String parentName,String childName){
		try{
			productLinkRemove.setInt(1, (parentName.hashCode()+childName.hashCode()) );
			productLinkRemove.executeUpdate();
			DBLogger.getInstance().print("Derby", "Unlinkging links successful ");
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "Error removing links "+e);
			e.printStackTrace();
		}
	}
	
	/*
	 * check if tables exist
	 * if so, then create groups,products,literature review, and reviews
	 * if not,
	 * then done.
	 */
	public void intialize(){
		/* 
		 * load the desired JDBC driver 
		 * Starts the derby service, but not database
		 * */
        loadDriver();
        try{
        	Properties props = new Properties(); // connection properties
            // providing a user name and password is optional in the embedded
            // and derby client frameworks
            props.put("user", "littool");
            props.put("password", "littool@919!");
            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
            conn = DriverManager.getConnection(protocol + litToolDB
                    + ";create=true", props);

            System.out.println("Connected to and created database " + litToolDB);

            // We want to control transactions manually. Autocommit is on by
            // default in JDBC.
            conn.setAutoCommit(false);
            Statement s = conn.createStatement();
            try{
            s.execute("create table groups( id int primary key, grpname varchar(40), litrevid int)");
            s.execute("create table litrevs( id int primary key, litrevname varchar(40), reviewid int, productid int)");
            s.execute("create table reviews( id int primary key, reviewname varchar(40), reviewfileloc varchar(160) )");
            s.execute("create table products( id int primary key, productname varchar(40), productfileloc varchar(160),producttitle varchar(80),productyear varchar(4) )");
            s.execute("create table productlinks( id int primary key, prodparentid int, productchildid int)");
            }catch(Exception e){
            	DBLogger.getInstance().print("Derby", "Tables aleady created, carry on");
            }
            sqlStatements.add(s);
            
            groupInsert = conn.prepareStatement("insert into groups values (?, ?, ?)");
            litrevInsert = conn.prepareStatement("insert into litrevs values (?, ?, ?, ?)");
            reviewInsert = conn.prepareStatement("insert into reviews values (?, ?, ?)");
            productInsert = conn.prepareStatement("insert into products values (?, ?, ?, ?, ?)");
            productLinkInsert = conn.prepareStatement("insert into productlinks values (?, ?, ?)");
            
            groupUpdate = conn.prepareStatement("update groups set id=?, grpname=? ,litrevid=? where id=?");
            litrevUpdate = conn.prepareStatement("update litrevs set id=?, litrevname=? , reviewid=? , productid=? where id=?");
            reviewUpdate = conn.prepareStatement("update reviews set id=?, reviewname=? ,reviewfileloc=? where id=?");
            productUpdate = conn.prepareStatement("update products set id=?, productname=? ,productfileloc=? , producttitle=?, productyear=? where id=?");
            productLinkUpdate = conn.prepareStatement("update productlinks set id=?, prodparentid=? ,productchildid=? where id=?");
            
            groupRemove = conn.prepareStatement("delete from groups where id=?");
            litrevRemove = conn.prepareStatement("delete from litrevs where id=?");
            reviewRemove = conn.prepareStatement("delete from reviews where id=?");
            productRemove = conn.prepareStatement("delete from products where id=?");
            productLinkRemove = conn.prepareStatement("delete from productlinks where id=?");
            
            DBLogger.getInstance().print("Derby", "Database Initialized, Populate Domain Model");
            populateDomainModel();
        }catch(Exception e){
        	DBLogger.getInstance().print("DERBY", "Unable to initialize database "+e);
        	System.exit(0);
        }
	}
	
	/*
	 * Replace ID
	 */
	public void replaceID(int type, String newName,String oldName) throws Exception{
		try{
			int newNameID=newName.hashCode();
			int oldNameID=oldName.hashCode();
			if(type==DerbyDBPersistance.PRODUCTS){
				ResultSet prodSet = sqlStatements.get(0).executeQuery("select * from products where id="+oldNameID);
				while(prodSet.next()){
					productUpdate.setInt(1, newNameID);
					productUpdate.setString(2, newName);
					productUpdate.setString(3, prodSet.getString(3));
					productUpdate.setString(4, prodSet.getString(4));
					productUpdate.setString(5, prodSet.getString(5));
					productUpdate.setInt(6, oldNameID);
					productUpdate.executeUpdate();
				}
				ResultSet litRevSet = sqlStatements.get(0).executeQuery("select * from litrevs where productid="+oldNameID);
				while(litRevSet.next()){
					litrevUpdate.setInt(1, litRevSet.getInt(1));
					litrevUpdate.setString(2, litRevSet.getString(2));
					litrevUpdate.setInt(3, litRevSet.getInt(3));
					litrevUpdate.setInt(4,newNameID);
					litrevUpdate.setInt(5, litRevSet.getInt(1));
					litrevUpdate.executeUpdate();
				}
				ResultSet prodSet2 = sqlStatements.get(0).executeQuery("select * from productlinks where prodparentid="+oldNameID);
				while(prodSet2.next()){
					DBLogger.getInstance().print("Derby", "changing parent id");
					productLinkUpdate.setInt(1, newNameID+prodSet2.getInt(3));
					productLinkUpdate.setInt(2, newNameID);
					productLinkUpdate.setInt(3, prodSet2.getInt(3));
					productLinkUpdate.setInt(4, oldNameID+prodSet2.getInt(3));
					productLinkUpdate.executeUpdate();
				}
				ResultSet prodSet3 = sqlStatements.get(0).executeQuery("select * from productlinks where productchildid="+oldNameID);
				while(prodSet3.next()){
					DBLogger.getInstance().print("Derby", "changing child id");
					productLinkUpdate.setInt(1, prodSet3.getInt(2)+newNameID);
					productLinkUpdate.setInt(2, prodSet3.getInt(2));
					productLinkUpdate.setInt(3, newNameID);
					productLinkUpdate.setInt(4, oldNameID+prodSet3.getInt(2));
					productLinkUpdate.executeUpdate();
				}
				DBLogger.getInstance().print("Derby", "Product Name change Successful-OK");
			}else
			if(type==DerbyDBPersistance.LITREVS){
				ResultSet prodSet = sqlStatements.get(0).executeQuery("select * from litrevs where id="+oldNameID);
				while(prodSet.next()){
					litrevUpdate.setInt(1, newNameID);
					litrevUpdate.setString(2, newName);
					litrevUpdate.setInt(3, prodSet.getInt(3));
					litrevUpdate.setInt(4, prodSet.getInt(4));
					litrevUpdate.setInt(5, oldNameID);
					litrevUpdate.executeUpdate();
				}
				ResultSet litRevSet = sqlStatements.get(0).executeQuery("select * from groups where litrevid="+oldNameID);
				while(litRevSet.next()){
					groupUpdate.setInt(1, litRevSet.getInt(1));
					groupUpdate.setString(2, litRevSet.getString(2));
					groupUpdate.setInt(3, newNameID);
					groupUpdate.setInt(4,litRevSet.getInt(1));
					groupUpdate.executeUpdate();
				}
				DBLogger.getInstance().print("Derby", "Lit Rev Name change Successful");
			}else
			if(type==DerbyDBPersistance.REVIEWS){
				ResultSet prodSet = sqlStatements.get(0).executeQuery("select * from reviews where id="+oldNameID);
				while(prodSet.next()){
					reviewUpdate.setInt(1, newNameID);
					reviewUpdate.setString(2, newName);
					reviewUpdate.setString(3, prodSet.getString(3));
					reviewUpdate.setInt(4, oldNameID);
					reviewUpdate.executeUpdate();
				}
				ResultSet litRevSet = sqlStatements.get(0).executeQuery("select * from litrevs where reviewid="+oldNameID);
				while(litRevSet.next()){
					litrevUpdate.setInt(1, litRevSet.getInt(1));
					litrevUpdate.setString(2, litRevSet.getString(2));
					litrevUpdate.setInt(3, newNameID);
					litrevUpdate.setInt(4, litRevSet.getInt(4));
					litrevUpdate.setInt(5, litRevSet.getInt(1));
					litrevUpdate.executeUpdate();
				}
				DBLogger.getInstance().print("Derby", "Review Name change Successful");
			}else
			if(type==DerbyDBPersistance.GROUPS){
				ResultSet litRevSet = sqlStatements.get(0).executeQuery("select * from groups where id="+oldNameID);
				while(litRevSet.next()){
					groupUpdate.setInt(1, newNameID);
					groupUpdate.setString(2, newName);
					groupUpdate.setInt(3, litRevSet.getInt(3));
					groupUpdate.setInt(4,litRevSet.getInt(1));
					groupUpdate.executeUpdate();
				}
				DBLogger.getInstance().print("Derby", "Group Name change Successful");
			}
			
		}catch(Exception e){
			System.out.println("Error replaceing id"+e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/*
	 * Populate Domain Model
	 */
	private void populateDomainModel(){
		try{
			ResultSet grpset = sqlStatements.get(0).executeQuery("select distinct grpname from groups");
			LinkedList<LiteratureGrouping> listGrp = new LinkedList<LiteratureGrouping>();
			while(grpset.next()){
				DBLogger.getInstance().print("Derby", "Create new Group");
				LiteratureGrouping grouping = new LiteratureGrouping(grpset.getString(1));
				grouping.setName(grpset.getString(1),false);
				listGrp.add(grouping);
			}
			LiteratureGroupManager.getInstance().setGroup(listGrp);
			
			ResultSet litrevset = sqlStatements.get(0).executeQuery("select distinct litrevname from litrevs");
			LinkedList<LiteratureReview> listLitRev = new LinkedList<LiteratureReview>();
			while(litrevset.next()){
				DBLogger.getInstance().print("Derby", "Create new Lit Rev");
				LiteratureReview litRev = new LiteratureReview(litrevset.getString(1));
				litRev.setName(litrevset.getString(1),false);
				listLitRev.add(litRev);
			}
			LiteratureReviewManager.getInstance().setLitRev(listLitRev);
			
			ResultSet reviewset = sqlStatements.get(0).executeQuery("select reviewname,reviewfileloc from reviews");
			LinkedList<Review> listReview = new LinkedList<Review>();
			while(reviewset.next()){
				DBLogger.getInstance().print("Derby", "Create new Review");
				Review review = new Review(reviewset.getString(1));
				review.setName(reviewset.getString(1),false);
				StringBuffer sb = new StringBuffer();
				try{
					File openFile = new File(reviewset.getString(2));
					BufferedReader reader = new BufferedReader(new FileReader(openFile));
					String text;
					while((text=reader.readLine())!=null){
						sb.append(text+"\n");
					}
				}catch(Exception e){
					DBLogger.getInstance().print("Derby", "Error opening info file of the review "+reviewset.getString(1));
				}
				review.setText(sb.toString());
				listReview.add(review);
			}
			LiteratureReviewManager.getInstance().setReview(listReview);
			
			ResultSet productset = sqlStatements.get(0).executeQuery("select productname,productfileloc,producttitle,productyear from products");
			LinkedList<LiteratureProduct> listProd = new LinkedList<LiteratureProduct>();
			while(productset.next()){
				DBLogger.getInstance().print("Derby", "Create new Product");
				LiteratureProduct product = new LiteratureProduct(productset.getString(1));
				product.setName(productset.getString(1),false);
				product.setFileLocation(productset.getString(2));
				product.setProductTitle(productset.getString(3));
				product.setProductYear(productset.getString(4));
				StringBuffer sb = new StringBuffer();
				try{
					File openFile = new File(productset.getString(2));
					BufferedReader reader = new BufferedReader(new FileReader(openFile));
					String text;
					while((text=reader.readLine())!=null){
						sb.append(text+"\n");
					}
				}catch(Exception e){
					DBLogger.getInstance().print("Derby", "Error opening info file of the product "+productset.getString(1));
				}
				product.setProductRef(sb.toString());
				listProd.add(product);
			}
			LiteratureProductManager.getInstance().setProduct(listProd);
			
			/*
			 * Ensure objects are linked together properly in the domain
			 */
			DBLogger.getInstance().print("Derby", "Created products and set their names ");
			
			/*
			 * Fix groups
			 */
			DBLogger.getInstance().print("Derby", "Now try to LInk lit revs into their groups");
			ResultSet comboRes = sqlStatements.get(0).executeQuery("select g.grpname, l.litrevname from groups g,litrevs l where g.litrevid=l.id");
			while(comboRes.next()){
				System.out.println("grp:"+comboRes.getString(1)+" litrev:"+comboRes.getString(2));
				LiteratureGrouping group = LiteratureGroupManager.getInstance().findLiteratureGrouping(comboRes.getString(1));
				LiteratureReview litRev = LiteratureReviewManager.getInstance().findLiteratureReview(comboRes.getString(2));
				group.add(litRev, false);//do not add table entry since table entry already exists
			}
			comboRes.close();
			
			/*
			 * Fix products to lit reviews
			 */
			ResultSet comboProd = sqlStatements.get(0).executeQuery("select p.productname, l.litrevname from products p, litrevs l where l.productid=p.id");
			while(comboProd.next()){
				System.out.println("lit:"+comboProd.getString(1)+" prod:"+comboProd.getString(2));
				LiteratureReview litRev = LiteratureReviewManager.getInstance().findLiteratureReview(comboProd.getString(2));
				LiteratureProduct product = LiteratureProductManager.getInstance().findLiteratureProduct(comboProd.getString(1));
				litRev.setLitProduct(product,false);
			}
			comboProd.close();
			
			/*
			 * Fix Reviews
			 */
			ResultSet comboReview = sqlStatements.get(0).executeQuery("select l.litrevname, r.reviewname from litrevs l, reviews r where l.reviewid=r.id");
			while(comboReview.next()){
				System.out.println("lit:"+comboReview.getString(1)+" review:"+comboReview.getString(2));
				LiteratureReview litRev = LiteratureReviewManager.getInstance().findLiteratureReview(comboReview.getString(1));
				Review review = LiteratureReviewManager.getInstance().findReview(comboReview.getString(2));
				litRev.addReview(review,false);
			}
			comboReview.close();
			conn.commit();
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "error populating domain "+e);
			e.printStackTrace();
		}
	}
	
	/*
	 * Select from a table
	 */
	public ResultSet selectAll(int type){
		try{
			if(type==DerbyDBPersistance.GROUPS){
				return sqlStatements.get(0).executeQuery("select * from groups");
			}else
			if(type==DerbyDBPersistance.PRODUCTS){
				return sqlStatements.get(0).executeQuery("select * from products");
			}else
			if(type==DerbyDBPersistance.LITREVS){
				return sqlStatements.get(0).executeQuery("select * from litrevs");
			}else
			if(type==DerbyDBPersistance.REVIEWS){
				return sqlStatements.get(0).executeQuery("select * from reviews");
			}
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "Error selecting"+e);
		}
		return null;
	}
	/*
	 * when new item is created, add this to table
	 */
	public void newTableEntry(int type,Object ref){
		try{
			if(type==DerbyDBPersistance.GROUPS){
				LiteratureGrouping group = (LiteratureGrouping)ref;
				groupInsert.setInt(1,group.getName().hashCode());
				groupInsert.setString(2, group.getName());
				groupInsert.setInt(3, 0);
				groupInsert.executeUpdate();
				DBLogger.getInstance().print("Derby", "Success Table Group Insert now ck");
			}else
			if(type==DerbyDBPersistance.PRODUCTS){
				LiteratureProduct product = (LiteratureProduct)ref;
				productInsert.setInt(1,product.getName().hashCode());
				productInsert.setString(2, product.getName());
				productInsert.setString(3, product.getFileLocation());
				productInsert.setString(4, product.getProductTitle());
				productInsert.setString(5, product.getProductYear());
				productInsert.executeUpdate();
				DBLogger.getInstance().print("Derby", "Success Table Product Insert now ck");
			}else
			if(type==DerbyDBPersistance.LITREVS){
				LiteratureReview litreview = (LiteratureReview)ref;
				litrevInsert.setInt(1,litreview.getName().hashCode());
				litrevInsert.setString(2, litreview.getName());
				litrevInsert.setInt(3, 0);
				litrevInsert.setInt(4, 0);
				litrevInsert.executeUpdate();
				DBLogger.getInstance().print("Derby", "Success Table Lit Review Insert now ck");
			}else
			if(type==DerbyDBPersistance.REVIEWS){
				Review review = (Review)ref;
				reviewInsert.setInt(1,review.getName().hashCode());
				reviewInsert.setString(2, review.getName());
				reviewInsert.setString(3, "");
				reviewInsert.executeUpdate();
				DBLogger.getInstance().print("Derby", "Success Table Review Insert now ck");
			}
			conn.commit();
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "Error adding table entry "+e);
			e.printStackTrace();
		}
	}
	
	public void updateReview(String revID,String fileLoc){
		try {
			reviewUpdate.setInt(1, revID.hashCode());
			reviewUpdate.setString(2,revID);
			reviewUpdate.setString(3, fileLoc);
			reviewUpdate.setInt(4, revID.hashCode());
			reviewUpdate.executeUpdate();
			conn.commit();
			DBLogger.getInstance().print("Derby", "Updated Review with new info and file loc");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateLiteratureProduct(String productID,String fileLoc,String title,String year){
		try{
			productUpdate.setInt(1,productID.hashCode());
			productUpdate.setString(2, productID);
			productUpdate.setString(3, fileLoc);
			productUpdate.setString(4, title);
			productUpdate.setString(5, year);
			productUpdate.setInt(6, productID.hashCode());
			productUpdate.executeUpdate();
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "Update lit product error ");
			e.printStackTrace();
		}
	}
	
	public void removeProduct(String productID){
		try{
			productRemove.setInt(1, productID.hashCode());
			productRemove.executeUpdate();
			DBLogger.getInstance().print("Derby", "removed product "+productID);
			/*
			 * Update lit review table to replace product id
			 */
			ResultSet set = sqlStatements.get(0).executeQuery("select * from litrevs where productID="+(productID.hashCode()));
			while(set.next()){
				litrevUpdate.setInt(1, set.getInt(1));
				litrevUpdate.setString(2,set.getString(2));
				litrevUpdate.setInt(3, set.getInt(3));
				litrevUpdate.setInt(4,0);
				litrevUpdate.setInt(5, set.getInt(1));
				litrevUpdate.executeUpdate();
			}
			ResultSet set2 = sqlStatements.get(0).executeQuery("select * from productlinks where prodparentid="+productID.hashCode()+" or productchildid="+productID.hashCode());
			while(set2.next()){
				productLinkRemove.setInt(1, set2.getInt(1));
				productLinkRemove.executeUpdate();
			}
			DBLogger.getInstance().print("Derby", "Updated literature reviews to remove product refs");
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "error removing product "+productID+" "+e);
			e.printStackTrace();
		}
	}
	
	public void removeLiteratureGrouping(String groupID){
		try{
			ResultSet set = sqlStatements.get(0).executeQuery("select * from groups where grpname='"+groupID+"'");
			while(set.next()){
				groupRemove.setInt(1, set.getInt(1));
				groupRemove.executeUpdate();
			}
			DBLogger.getInstance().print("Derby", "Remove grp "+groupID);
			conn.commit();
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "error Removed Lit Grp "+e);
			e.printStackTrace();
		}
	}
	
	public void removeLiteratureReview(String litRevID){
		try{
			ResultSet set = sqlStatements.get(0).executeQuery("select * from litrevs where litrevname='"+litRevID+"'");
			while(set.next()){
				litrevRemove.setInt(1, set.getInt(1));
				litrevRemove.executeUpdate();
				DBLogger.getInstance().print("Derby", "REmoved Lit Review "+litRevID.hashCode());
			}
			/*
			 * Now update group table entries which may have referenced the literature review
			 */
			ResultSet set1 = sqlStatements.get(0).executeQuery("select distinct grpname from groups where litrevid="+litRevID.hashCode());
			LinkedList<String> grpNames = new LinkedList<String>();
			while(set1.next()){
				grpNames.add(set1.getString(1));
			}
			
			for(int i=0;i<grpNames.size();i++){
				String groupName = grpNames.get(i);
				ResultSet set2 = sqlStatements.get(0).executeQuery("select * from groups where grpname='"+groupName+"'");
				int grpSize=0;
				while(set2.next()){
					grpSize++;
				}
				if(grpSize==1){
					PreparedStatement grpStatement = conn.prepareStatement("update groups set litrevid=0 where litrevid="+litRevID.hashCode()+" and grpname='"+groupName+"'");
					grpStatement.executeUpdate();
					conn.commit();
				}else{
					PreparedStatement grpStatement = conn.prepareStatement("delete from groups where litrevid="+litRevID.hashCode());
					grpStatement.executeUpdate();
					conn.commit();
				}
			}
			ResultSet set3 = sqlStatements.get(0).executeQuery("select * from groups where litrevid="+litRevID.hashCode());
			while(set3.next()){
				groupRemove.setInt(1, set3.getInt(1));
				groupRemove.executeUpdate();
				conn.commit();
			}
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "Error removing Literature review"+e);
			e.printStackTrace();
		}
	}
	
	public void removeReview(String litRevID,String revID){
		try{
			reviewRemove.setInt(1, revID.hashCode());
			reviewRemove.executeUpdate();
			conn.commit();
			ResultSet set = sqlStatements.get(0).executeQuery("select * from litrevs where litrevname='"+litRevID+"'");
			int setCount=0;
			while(set.next()){
				setCount++;
			}
			int revIDInt = revID.hashCode();
			DBLogger.getInstance().print("Derby", "Removed review "+revID+" FetchSize:"+setCount);
			ResultSet set2 = sqlStatements.get(0).executeQuery("select * from litrevs where reviewid="+revIDInt+"");
			
			while(set2.next()){
				if(setCount==1){
					litrevUpdate.setInt(1, set2.getInt(1));
					litrevUpdate.setString(2, set2.getString(2));
					litrevUpdate.setInt(3, 0);
					litrevUpdate.setInt(4, set2.getInt(4));
					litrevUpdate.setInt(5, set2.getInt(1));
					litrevUpdate.executeUpdate();
					conn.commit();
					return;
				}else{
					litrevRemove.setInt(1, set2.getInt(1));
					litrevRemove.executeUpdate();
					conn.commit();
				}
			}
			//conn.commit();
			DBLogger.getInstance().print("Derby", "update lit rev "+litRevID);
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "error removing review "+revID+" from lit revb "+litRevID+":"+e);
			e.printStackTrace();
		}
	}
	
	public void updateGroupRemoveLitRev(String grpID,String litRevID){
		try{
			ResultSet set = sqlStatements.get(0).executeQuery("select * from groups where grpname='"+grpID+"'");
			int numberEntries=0;
			while(set.next()){
				numberEntries++;
			}
			DBLogger.getInstance().print("Derby", "There are "+numberEntries+" in set");
			ResultSet set2 = sqlStatements.get(0).executeQuery("select * from groups where grpname='"+grpID+"' and litrevid="+litRevID.hashCode());
			while(set2.next()){
				if(numberEntries==1){
					groupUpdate.setInt(1, set2.getInt(1));
					groupUpdate.setString(2, set2.getString(2));
					groupUpdate.setInt(3, 0);
					groupUpdate.executeUpdate();
					conn.commit();
					return;
				}else{
					groupRemove.setInt(1, set2.getInt(1));
					groupRemove.executeUpdate();
					conn.commit();
					return;
				}
			}
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "error removing lit rev from grp "+litRevID);
			e.printStackTrace();
		}
	}
	
	public void updateGroupAddLitRev(String grpID,String litRevID ){
		try{
			/*
			 * Check if this group has a blank for lit rev if so, set the ID
			 */
			ResultSet set1 = sqlStatements.get(0).executeQuery("select * from groups where grpname='"+grpID+"'");
			while(set1.next()){
				int litRevIDInt=set1.getInt(3);
				if(litRevIDInt==0){
					groupUpdate.setInt(1, set1.getInt(1));
					groupUpdate.setString(2, set1.getString(2));
					groupUpdate.setInt(3, litRevID.hashCode());
					groupUpdate.setInt(4, set1.getInt(1));
					groupUpdate.executeUpdate();
					DBLogger.getInstance().print("Derby", "Group Update to add Lit Rev Complete");
					conn.commit();
					return;
				}
			}
			/*
			 * Need to add new Group entry with lit rev id
			 */
			groupInsert.setInt(1, litRevID.hashCode()+grpID.hashCode());
			groupInsert.setString(2, grpID);
			groupInsert.setInt(3, litRevID.hashCode());
			groupInsert.executeUpdate();
			conn.commit();
			DBLogger.getInstance().print("Derby", "Group added table entry to add Lit Rev Complete");
			
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "Error adding groups lit rev"+e);
			e.printStackTrace();
		}
	}

	public void updateLitRevAddReview(String name,String revID,String prodID ){
		try {
			/*
			 * First check if any lit rev enties do not have a revId yet.
			 */
			ResultSet res3 = sqlStatements.get(0).executeQuery("select * from litrevs where litrevname='"+name+"'");
			while(res3.next()){
				int revid = res3.getInt(3);
				if(revid==0){
					litrevUpdate.setInt(1, res3.getInt(1));
					litrevUpdate.setString(2, res3.getString(2));
					litrevUpdate.setInt(3, revID.hashCode());
					litrevUpdate.setInt(4, res3.getInt(4));
					litrevUpdate.setInt(5, res3.getInt(1));
					litrevUpdate.executeUpdate();
					DBLogger.getInstance().print("Derby", "Added a review to literature review found empty spot");
					conn.commit();
					return;
				}
			}
			/*
			 * If unsucessful above. do this
			 */
			ResultSet res2 = sqlStatements.get(0).executeQuery("select * from products where productname='"+prodID+"'");
			while(res2.next()){
				int prodIdex=res2.getInt(1);
				litrevInsert.setInt(1, revID.hashCode());
				litrevInsert.setString(2, name);
				litrevInsert.setInt(3, revID.hashCode());
				litrevInsert.setInt(4, prodIdex);
				litrevInsert.executeUpdate();
				DBLogger.getInstance().print("Derby", "Added a review to literature review, create new Lit Rev to do so");
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateLitRevSetProductID(String litrevID,String prodID ){
		try {
			ResultSet prodSet = sqlStatements.get(0).executeQuery("select * from products where productname='"+prodID+"'");
			prodSet.next();
			int prodIDInt=prodSet.getInt(1);
			ResultSet res = sqlStatements.get(0).executeQuery("select * from litrevs where litrevname='"+litrevID+"'");
			while(res.next()){
				litrevUpdate.setInt(1, res.getInt(1));
				litrevUpdate.setString(2, res.getString(2));
				litrevUpdate.setInt(3, res.getInt(3));
				litrevUpdate.setInt(4, prodIDInt);
				litrevUpdate.setInt(5, res.getInt(1));
				DBLogger.getInstance().print("Derby Update prod ", ""+res.getInt(1)+","+res.getString(2)+","+res.getInt(3)+","+prodIDInt+","+res.getInt(1));
				litrevUpdate.executeUpdate();
				DBLogger.getInstance().print("Derby", "Changed LitRev entry with new product ID");
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	private void loadDriver() {
        try {
            Class.forName(driver).newInstance();
            System.out.println("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver " + driver);
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            System.err.println(
                        "\nUnable to instantiate the JDBC driver " + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            System.err.println(
                        "\nNot allowed to access the JDBC driver " + driver);
            iae.printStackTrace(System.err);
        }
    }
	
	public void showTable(int type){
		try{
			if(type==domain.System.Group){
				ResultSet set = this.sqlStatements.get(0).executeQuery("select * from groups");
				System.out.println("****************Groups****************");
				System.out.println("| id \t\t| grpname \t| litrevid \t|");
				while(set.next()){
					System.out.println("|"+set.getInt(1)+"\t|"+set.getString(2)+"\t|"+set.getInt(3)+"\t|");
				}
				System.out.println("**************************************");
			}else
			if(type==domain.System.LitRev){
				ResultSet set = this.sqlStatements.get(0).executeQuery("select * from litrevs");
				System.out.println("****************Lit Revs****************");
				System.out.println("| id \t\t| litrevname \t| revid \t| prodid \t|");
				while(set.next()){
					System.out.println("|"+set.getInt(1)+"\t|"+set.getString(2)+"\t|"+set.getInt(3)+"\t|"+set.getInt(4)+"\t|");
				}
				System.out.println("**************************************");
			}else
			if(type==domain.System.Rev){
				ResultSet set = this.sqlStatements.get(0).executeQuery("select * from reviews");
				System.out.println("****************Reviews****************");
				System.out.println("| id \t\t| reviewname \t| file location \t|");
				while(set.next()){
					System.out.println("|"+set.getInt(1)+"\t|"+set.getString(2)+"\t|"+set.getString(3)+"\t|");
				}
				System.out.println("**************************************");
			}else
			if(type==domain.System.LitProd){
				ResultSet set = this.sqlStatements.get(0).executeQuery("select * from products");
				System.out.println("****************Lit Products****************");
				System.out.println("| id \t\t| productname \t| file loc \t| title \t| year \t");
				while(set.next()){
					System.out.println("|"+set.getInt(1)+"\t|"+set.getString(2)+"\t|"+set.getString(3)+"\t|"+set.getString(4)+"\t|"+set.getString(5)+"\t|");
				}
				System.out.println("**************************************");
			}else
			if(type==domain.System.ProdLink){
				ResultSet set = this.sqlStatements.get(0).executeQuery("select * from productlinks");
				System.out.println("****************Product Links****************");
				System.out.println("| id \t\t| prodparentname \t| prodchildid \t|");
				while(set.next()){
					System.out.println("|"+set.getInt(1)+"\t|"+set.getInt(2)+"\t|"+set.getInt(3)+"\t|");
				}
				System.out.println("**************************************");
			}
				
			
		}catch(Exception e){
			DBLogger.getInstance().print("Derby", "Unable to show table "+type);
		}
	}
	
	public void shutdown(){
		 try
         {
			 conn.commit();
			 conn.close();
             DBLogger.getInstance().print("Derby", "Shutting Down Derby and Databases");
             DriverManager.getConnection("jdbc:derby:;shutdown=true");
         }
         catch (SQLException se)
         {
        	 //DBLogger.getInstance().print("Derby", "problem shutting down "+se);
        	 //se.printStackTrace();
         }
	}
}
