/**
 * Class BlockStack
 * Implements character block stack and operations upon it.
 *
 * $Revision: 1.4 $
 * $Last Revision Date: 2019/02/02 $
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca;
 * Inspired by an earlier code by Prof. D. Probst

 */
class BlockStack
{
	/**
	 * # of letters in the English alphabet + 2
	 */
	public static final int MAX_SIZE = 28;

	/**
	 * Default stack size
	 */
	public static final int DEFAULT_SIZE = 6;

	/**
	 * Current size of the stack
	 */
	private int iSize = DEFAULT_SIZE;	//task II : modiy visibility from public to private
	
	
	
	/**
	 * Current top of the stack
	 */
	private int iTop  = 3;		//task II : modiy visibility from public to private

	/**
	 * stack[0:5] with four defined values
	 */
	//public char acStack[] = new char[] {'a', 'b', 'c', 'd', '$', '$'};
	private char acStack[] = new char[] {'a', 'b', 'c', 'd', '*', '*'};	//task I, task II : represent empty position with '*'
																		//modify visibility from public to private
	
	
	
	/**
	 *	task I : declare a stack access counter
	 */
	private static int stackAccessCounter;	//task I, task II : create a counter to record the access into the stack and make it private
	
	/**
	 *	task II : declare getter and setter methods for member variables  
	 */
	public int getiSize() {
		return iSize;
	}

	public void setiSize(int iSize) {
		this.iSize = iSize;
	}

	public int getiTop() {
		return iTop;
	}

	public void setiTop(int iTop) {
		this.iTop = iTop;
	}

	public char[] getAcStack() {
		return acStack;
	}

	public void setAcStack(char[] acStack) {
		this.acStack = acStack;
	}

	public int getAccessCounter() {		//getter method for stack access counter
		return stackAccessCounter;
	}
	
	/**
	 * Default constructor
	 */
	public BlockStack()
	{
		stackAccessCounter = 0;	//initialize stack access counter to 0
	}

	/**
	 * Supplied size
	 */
	public BlockStack(final int piSize)
	{

		stackAccessCounter = 0;	//initialize stack access counter to 0
                if(piSize != DEFAULT_SIZE)
		{
			this.acStack = new char[piSize];

			// Fill in with letters of the alphabet and keep
			// 2 free blocks
			for(int i = 0; i < piSize - 2; i++)
				this.acStack[i] = (char)('a' + i);

			//this.acStack[piSize - 2] = this.acStack[piSize - 1] = '$';
			this.acStack[piSize - 2] = this.acStack[piSize - 1] = '*';	//task I : represent empty position with '*'

			this.iTop = piSize - 3;
                        this.iSize = piSize;
		}
	}

	/**
	 * Picks a value from the top without modifying the stack
	 * @return top element of the stack, char
	 */
	public char pick()
	{
		stackAccessCounter++;	//increment stackAccessCounter by 1
		return this.acStack[this.iTop];
	}

	/**
	 * Returns arbitrary value from the stack array
	 * @return the element, char
	 */
	public char getAt(final int piPosition)
	{
		stackAccessCounter++;	//task I : increment stackAccessCounter by 1
		return this.acStack[piPosition];
	}

	/**
	 * Standard push operation with full stack exception check
	 */
	public void push(final char pcBlock) throws stackException	//task II : use exception to do the boundary check
	{
		if(this.iTop==(this.iSize-1))
			throw new stackException(2);	//task II : use exception to do the boundary check
		else {
			this.acStack[++this.iTop] = pcBlock;
			stackAccessCounter++;	//task I : increment stackAccessCounter by 1
			System.out.println("One element has been succefully pushed!");	//task I : printing message of a successful push
		}
		
	}
	/**
	 * task II : push operation without argument
	 */
	public void push() throws stackException {	//task II : use exception to do the boundary check
		if(this.iTop==(this.iSize-1))
			throw new stackException(2);	//task II : use exception to do the boundary check
		else {
			this.acStack[++this.iTop] = 'a';
			stackAccessCounter++;	//task I : increment stackAccessCounter by 1
			System.out.println("A character 'a' has been succefully pushed!");	//task I : printing message of a successful push
		}
	}

	/**
	 * Standard pop operation with empty stack exception check
	 * @return ex-top element of the stack, char
	 */
	public char pop() throws stackException	//task II : use exception to do the boundary check
	{
		if(this.iTop<0) {
			throw new stackException(1);	//task II : use exception to do the boundary check
		}
		else {
			char cBlock = this.acStack[this.iTop];
			//this.acStack[this.iTop--] = '$'; // Leave prev. value undefined
			this.acStack[this.iTop--] = '*'; //task I :¡¡represent empty position with '*'
			stackAccessCounter++;	//task I : decrement stackAccessCounter by 1
			System.out.println("One element has been succefully poped!");	//task I : printing message of a successful pop
			if(this.getiTop()<0) {	//if the index of the top of stack less than 0, it means the stack is empty
				this.push();	//task II : if the stack is empty, call push to add 'a' into the empty stack
			}
			return cBlock;
		}
	}
	
	/**
	 * check if the stack is empty
	 * @return a boolean value 
	 */
	
	public boolean isEmpty() {
		boolean flag = false;
		if(this.getiTop()<0)
			flag = true;
		return flag;
	}
}

// EOF
