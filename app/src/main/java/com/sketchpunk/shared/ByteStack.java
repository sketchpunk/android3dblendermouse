package com.sketchpunk.shared;
import java.util.ArrayList;

/* TESTING CODE
		byte[] bytes = "rawr~woot~amazing".getBytes();
		ByteStack stack = new ByteStack(50);

		stack.put(bytes);
		stack.getAvailable('~');
		//System.out.println(stack.getTill('~', true));
		//System.out.println(stack.getTill('~', true));
		//System.out.println(stack.getTill('~', true));
		//System.out.println(stack.getTill('~', true));
		//System.out.println(stack.getTill('~', true));
		//System.out.println(stack.getTill('~', true));
		System.out.println(stack.getRemaining());
 */


/* When reading data from a serial connection, it requires several reads to get the message at times
	So this class is to buffer the results and makes it easy to get each chunk of data by the
	delimiter being used in the stream.
 */
public class ByteStack{
	//region Properties + Constructor
	private byte[] mArray;		//Data Storage
	private int mAllocated = 0;	//Size of storage
	private int mWritePos = 0;	//THe position that the next write should start on
	private int mReadPos = 0;	//The position that the next read should start on.

	public ByteStack(int size){
		mArray = new byte[size];
		mAllocated = size;
	}//func
	//endregion

	//region Writing to stack

	//copy array data to stack
	public boolean put(byte[] ary){ return put(ary,0,ary.length); }
	public boolean put(byte[] ary,int offset,int len){
		if(len + mWritePos > mAllocated) return false; //Can not write passed allocated space.

		int stop = offset+len;
		for(int i=offset; i < stop; i++){
			//System.out.println((char) ary[i]);
			mArray[mWritePos] = ary[i];
			mWritePos++;
		}//for

		return true;
	}//func
	//endregion

	//region Reading from stack

	//Read all the data till a character is found.
	//Stack cleaning can be controlled, allows dev to do a few readings
	//in a row before wasting processing time re-shifting the data.
	public String getTill(char chr,boolean doClear){
		if(mWritePos == 0 || mReadPos >= mWritePos) return null;

		//System.out.println("READTILL START");
		int i = mReadPos;
		boolean isFound = false;
		String rtn = null;

		for(; i < mWritePos; i++){
			if(mArray[i] == chr){
				rtn = new String(mArray,mReadPos,i-mReadPos);
				//System.out.format("Found read %d %d %d\n", mReadPos,i , mWritePos);
				//System.out.println(rtn);
				isFound = true;
				mReadPos = i+1;// Set the position start of the next read.
				break;
			}//if
		}//f0r

		if(isFound){
			if(mReadPos >= mWritePos){
				//System.out.println("EOB - Reset");
				mReadPos = 0;
				mWritePos = 0;
			}else if(doClear) clearRead();
		}//if

		return rtn;
	}//func


	public String[] getAvailable(char chr){
		ArrayList<String> ary = new ArrayList<String>();
		String txt = "";

		//Read all available string that ends with char.
		while( (txt = getTill(chr,false)) != null ){
			ary.add(txt);
			//System.out.println(txt);
		}//while

		if(ary.size() > 0){
			clearRead(); //do cleanup on read process.
			return ary.toArray(new String[ary.size()]);
		}//if

		return null;
	}//func

	//get whatever is left in the stack
	public String getRemaining(){
		if(mWritePos == 0) return null;

		String rtn = null;
		if(mReadPos < mWritePos){
			//System.out.format("Remaining %d\n", mWritePos - mReadPos);
			//System.out.format("Found read %d %d \n", mReadPos,mWritePos);
			rtn = new String(mArray, mReadPos, mWritePos - mReadPos);
			//System.out.println("Da End " + txt);
		}//if

		//Reset all positions since the rest of the data has been read.
		mWritePos = 0;
		mReadPos = 0;

		return rtn;
	}//func
	//endregion

	//region Stack Maintenance
	//This shifts all the unread data to the beginning of the array
	//If using this object alot, then there needs to be some cleanup
	//Or the allocated space will be used up.
	public void clearRead(){
		if(mWritePos == 0) return; //No need to do any cleaning. Writing is set back to start.
		//System.out.println("CLEAR");
		int pos = -1;
		for(int i=mReadPos; i < mWritePos; i++) mArray[++pos] = mArray[i];

		mWritePos = pos+1;
		mReadPos = 0;
	}//func
	//endregion
}//cls