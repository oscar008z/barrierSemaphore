// Import (aka include) some stuff.
import common.*;

/**
 * Class BlockManager
 * Implements character block "manager" and does twists with threads.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca;
 * Inspired by previous code by Prof. D. Probst
 *
 * $Revision: 1.5 $
 * $Last Revision Date: 2019/02/02 $

 */
public class BlockManager
{
	/**
	 * The stack itself
	 */
	private static BlockStack soStack = new BlockStack();

	/**
	 * Number of threads dumping stack
	 */
	private static final int NUM_PROBERS = 4;

	/**
	 * Number of steps they take
	 */
	private static int siThreadSteps = 5;

	/**
	 * For atomicity
	 */
	private static Semaphore mutex = new Semaphore(1);	//task III : instantiate object of semaphore as mutex

	/*
	 * For synchronization
	 */

	/**
	 * s1 is to make sure phase I for all is done before any phase II begins
	 */
	private static Semaphore s1 = new Semaphore(-8);	//define the s1 semaphore as barrier to phase I

	/**
	 * s2 is for use in conjunction with Thread.turnTestAndSet() for phase II proceed
	 * in the thread creation order
	 */
	private static Semaphore s2 = new Semaphore(0);


	// The main()
	public static void main(String[] argv) throws stackException
	{
		try
		{
			// Some initial stats...
			System.out.println("Main thread starts executing.");
			System.out.println("Initial value of top = " + soStack.getiTop() + ".");	//task II : use getter to get private class variables
			System.out.println("Initial value of stack top = " + soStack.pick() + ".");
			System.out.println("Main thread will now fork several threads.");

			/*
			 * The birth of threads
			 */
			AcquireBlock ab1 = new AcquireBlock();
			AcquireBlock ab2 = new AcquireBlock();
			AcquireBlock ab3 = new AcquireBlock();

			System.out.println("main(): Three AcquireBlock threads have been created.");

			ReleaseBlock rb1 = new ReleaseBlock();
			ReleaseBlock rb2 = new ReleaseBlock();
			ReleaseBlock rb3 = new ReleaseBlock();

			System.out.println("main(): Three ReleaseBlock threads have been created.");

			// Create an array object first
			CharStackProber	aStackProbers[] = new CharStackProber[NUM_PROBERS];

			// Then the CharStackProber objects
			for(int i = 0; i < NUM_PROBERS; i++)
				aStackProbers[i] = new CharStackProber();

			System.out.println("main(): CharStackProber threads have been created: " + NUM_PROBERS);

			/*
			 * Twist 'em all
			 */
			ab1.start();
			aStackProbers[0].start();
			rb1.start();
			aStackProbers[1].start();
			ab2.start();
			aStackProbers[2].start();
			rb2.start();
			ab3.start();
			aStackProbers[3].start();
			rb3.start();

			System.out.println("main(): All the threads are ready.");

			/*
			 * Wait by here for all forked threads to die
			 */
			ab1.join();
			ab2.join();
			ab3.join();

			rb1.join();
			rb2.join();
			rb3.join();

			for(int i = 0; i < NUM_PROBERS; i++)
				aStackProbers[i].join();

			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + soStack.getiTop() + ".");//task II : use getter to get private class variables
			System.out.println("Final value of stack top = " + soStack.pick() + ".");
			System.out.println("Final value of stack top-1 = " + soStack.getAt(soStack.getiTop() - 1) + ".");//task II : use getter to get private class variables
			System.out.println("Stack access count = " + soStack.getAccessCounter());	//task I : print out the value of counter

			System.exit(0);
		}
		catch(InterruptedException e)
		{
			System.err.println("Caught InterruptedException (internal error): " + e.getMessage());
			e.printStackTrace(System.err);
		}
		catch(Exception e)
		{
			reportException(e);
		}
		finally
		{
			System.exit(1);
		}
	} // main()


	/**
	 * Inner AcquireBlock thread class.
	 */
	static class AcquireBlock extends BaseThread
	{
		/**
		 * A copy of a block returned by pop().
                 * @see BlocStack#pop()
		 */
		private char cCopy;
		
		public void run()
		{
			
			System.out.println("AcquireBlock thread [TID=" + this.iTID + "] starts executing.");

			phase1();
			//
			try
			{	
				/**
				 * task iV : the beginning of first step of barrier synchronization : all 10 threads finish phase1 before executing phase2 
				 * */
				if(this.iTID==1) {	
					s1.Wait();	//step 2: the thread1 wait the semaphore s1 for the access of execution
					s2.Signal();	//step 4: when thread1 have the receive s1 semaphore, it gives grant the permission of semaphore s2 to threads 2-10
				}
				else {
					s1.Signal();	//step 1-9: threads 2-10 increase signal of s1 semaphore one by one until s1 signal becomes 1 
					s2.Wait();	//step 3: threads 2-10 wait until receive the permission from s2 semaphore for execution 
					s2.Signal();	//step5: when receive signal of s2, ore of thread 2-10 give another signal of s2 to other threads2-10 for further execution
				}
				/**
				 * task iV : the end of first step of barrier synchronization : all 10 threads finish phase1 before executing phase2 
				 * */
				
				mutex.Wait();	//task III : use mutex to lock the following block
				System.out.println("AcquireBlock thread [TID=" + this.iTID + "] requests Ms block.");
				
				this.cCopy = soStack.pop();
				
				System.out.println
				(
					"AcquireBlock thread [TID=" + this.iTID + "] has obtained Ms block " + this.cCopy +
					" from position " + (soStack.getiTop() + 1) + "."
				);


				System.out.println
				(
					"Acq[TID=" + this.iTID + "]: Current value of top = " +
					soStack.getiTop() + "."
				);

				System.out.println
				(
					"Acq[TID=" + this.iTID + "]: Current value of stack top = " +
					soStack.pick() + "."
				);
				mutex.Signal();	//task III : use mutex to unlock the above block
			}
			catch(stackException e) {	//task II : build try block for the stackException
				System.out.println(e.toString());
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			//
			
			/**
			 * task V : the beginning of second step of barrier synchronization : all 10 threads execute phase2 in a ascending order in terms of their thread id 
			 */
			while(true) {	//use while loop to keep all the threads polling the state of turn and semaphore s2 
				if(s2.isLocked()==false) {	//if the semaphore s2 is released, open the execution to all threads
					mutex.Wait();	//use mutex to grant access to only one thread to avoid the atomic problem 
					if(this.turnTestAndSet()==true) {	//check if the permitted thread has the right turn
						s2.Wait();	//if the thread has the right turn, lock the semaphore s2 to keep other threads from entering
						phase2();	//execute phase2
						s2.Signal();	//when complete phase2 give unlock the semaphore s2
						mutex.Signal();	//unlock mutex to let other threads enter
						break;	//quit the while loop
					}
					else {
						//System.out.println("thread-" + this.iTID + " has attempted but waiting for its turn to finish PHASE II");
						mutex.Signal();	//if the thread doesn't have the right turn, release the mutex to let other threads enter
					}
				}
			}
			
			/**
			 * task V : the end of second step of barrier synchronization : all 10 threads execute phase2 in a ascending order in terms of their thread id 
			 */
			System.out.println("AcquireBlock thread [TID=" + this.iTID + "] terminates.");
			
		}
	} // class AcquireBlock


	/**
	 * Inner class ReleaseBlock.
	 */
	static class ReleaseBlock extends BaseThread
	{
		/**
		 * Block to be returned. Default is 'a' if the stack is empty.
		 */
		private char cBlock = 'a';

		public void run()
		{
			
			System.out.println("ReleaseBlock thread [TID=" + this.iTID + "] starts executing.");


			phase1();

			//
			try
			{
				/**
				 * task iV : the beginning of first step of barrier synchronization : all 10 threads finish phase1 before executing phase2 
				 * */
				if(this.iTID==1) {
					s1.Wait();
					s2.Signal();
				}
				else {
					s1.Signal();
					s2.Wait();
					s2.Signal();
				}
				/**
				 * task iV : the end of first step of barrier synchronization : all 10 threads finish phase1 before executing phase2 
				 * */
				
				
				mutex.Wait();	//task III : use mutex to lock the following block
				if(soStack.isEmpty() == false)
					this.cBlock = (char)(soStack.pick() + 1);


				System.out.println
				(
					"ReleaseBlock thread [TID=" + this.iTID + "] returns Ms block " + this.cBlock +
					" to position " + (soStack.getiTop() + 1) + "."
				);

				soStack.push(this.cBlock);

				System.out.println
				(
					"Rel[TID=" + this.iTID + "]: Current value of top = " +
					soStack.getiTop() + "."
				);

				System.out.println
				(
					"Rel[TID=" + this.iTID + "]: Current value of stack top = " +
					soStack.pick() + "."
				);
				mutex.Signal();	//task III : use mutex to unlock the above block
				
			}
			catch(stackException e) {
				System.out.println(e.toString());
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			//
			
			/**
			 * task V : the beginning of second step of barrier synchronization : all 10 threads execute phase2 in a ascending order in terms of their thread id 
			 */
			while(true) {
				if(s2.isLocked()==false) {
					mutex.Wait();
					if(this.turnTestAndSet()==true) {
						s2.Wait();
						phase2();
						s2.Signal();
						mutex.Signal();
						break;
					}
					else {
						//System.out.println("thread-" + this.iTID + " has attempted but waiting for its turn to finish PHASE II");
						mutex.Signal();
					}
				}
			}
			/**
			 * task V : the end of second step of barrier synchronization : all 10 threads execute phase2 in a ascending order in terms of their thread id 
			 */

			System.out.println("ReleaseBlock thread [TID=" + this.iTID + "] terminates.");
			
		}
	} // class ReleaseBlock


	/**
	 * Inner class CharStackProber to dump stack contents.
	 */
	static class CharStackProber extends BaseThread
	{
		public void run()
		{
			
			phase1();

			//
			try
			{
				/**
				 * task iV : the beginning of first step of barrier synchronization : all 10 threads finish phase1 before executing phase2 
				 * */
				if(this.iTID==1) {
					s1.Wait();
					s2.Signal();
				}
				else {
					s1.Signal();
					s2.Wait();
					s2.Signal();
						
				}
				/**
				 * task iV : the end of first step of barrier synchronization : all 10 threads finish phase1 before executing phase2 
				 * */
				
				mutex.Wait();	//task III : use mutex to lock the following block
				for(int i = 0; i < siThreadSteps; i++)
				{
					System.out.print("Stack Prober [TID=" + this.iTID + "]: Stack state: ");

					// [s] - means ordinay slot of a stack
					// (s) - current top of the stack
					for(int s = 0; s < soStack.getiSize(); s++)
						System.out.print
						(
							(s == BlockManager.soStack.getiTop() ? "(" : "[") +
							BlockManager.soStack.getAt(s) +
							(s == BlockManager.soStack.getiTop() ? ")" : "]")
						);

					System.out.println(".");

				}
				mutex.Signal();	//task III : use mutex to unlock the above block
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}
			//
			
			//
			/**
			 * task V : the beginning of second step of barrier synchronization : all 10 threads execute phase2 in a ascending order in terms of their thread id 
			 */
			while(true) {
				if(s2.isLocked()==false) {
					mutex.Wait();
					if(this.turnTestAndSet()==true) {
						s2.Wait();
						phase2();
						s2.Signal();
						mutex.Signal();
						break;
					}
					else {
						//System.out.println("thread-" + this.iTID + " has attempted but waiting for its turn to finish PHASE II");
						mutex.Signal();
					}
				}
			}
			/**
			 * task V : the end of second step of barrier synchronization : all 10 threads execute phase2 in a ascending order in terms of their thread id 
			 */
			
			System.out.println("CharStackProber thread [TID=" + this.iTID + "] terminates.");
		}
	} // class CharStackProber


	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	private static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
} // class BlockManager

// EOF
