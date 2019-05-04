
/**
 * 
 * @author oscar
 * the stackException class for assignment 2 task II of comp346
 */

public class stackException extends Exception {
	private int typeOfException;
	
	public stackException(int typeOfException) {
		this.typeOfException = typeOfException;
	}
	
	public String toString(){
		if(this.typeOfException==1)
			return "Empty Stack !!!";
		else if(this.typeOfException==2)
			return "Full Stack !!!";
		else
			return "an exception has occered!!!";
	}
}
