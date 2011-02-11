package domain;

public class Review {
	private String revName;
	private String text = new String();
	public Review(String name){
		revName=new String("Review:"+this.hashCode());
	}
	
	public void setName(String name){
		revName=name;
	}
	
	public String getName(){
		return revName;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public String toString(){
		return revName;
	}
}
