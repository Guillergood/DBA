package Practica_3.Util;

import java.lang.reflect.Array;

/**
 *
 * @author Bruno García Trípoli
 * @param <T>
 */
public class Matrix<T> {
	private final int rows,cols;
	private T[][] data;

	private final int THREAD_COUNT;
	private final int step;
	private Thread thread[];

	public static interface Operator<T>{
            public T operate(int x,int y,T value);
	}

	public Matrix(int rows,int cols,T default_value) {
            this(rows,cols,default_value.getClass());

            foreach((x,y,value)->default_value);
	}
        
        public Matrix(int rows,int cols,T default_value,int thread_count) {
            this(rows,cols,default_value.getClass(),thread_count);

            foreach((x,y,value)->default_value);
	}

	public Matrix(int rows,int cols,Class<?> type) {
            this(rows,cols,type,3);
	}
        
        public Matrix(int rows,int cols,Class<?> type,int thread_count) {
            this.rows = rows;
            this.cols = cols;
            THREAD_COUNT = thread_count;
            thread = new Thread[THREAD_COUNT];
            step = (rows*cols)/(THREAD_COUNT+1);
            
            data =  (T[][]) Array.newInstance(type, rows, cols);
	}

	public int getRowsNum() {return rows;}

	public int getColsNum() {return cols;}

        /**
         * 
         * @param x row index
         * @param y col index
         * @return The T value at pos [x,y]
         */
	public T get(int x,int y) {return data[y][x]; }

        /**
         * Sets T value at pos [x,y]
         * @param x row index
         * @param y col index
         * @param value New Value
         */
	public void set(int x,int y,T value) {data[y][x]=value;}

        /**
         * Fill a region, a square, where that square is value and his center is placed at [x,y].
         * Note: This method only guarantees the expected result on Odd Squared Matrix. (e.g. value[3][3])
         * @param x row index
         * @param y col index
         * @param value Square Matrix.           
         */
	public void ranged_fill(int x,int y,T[][] value) {
		int local_step = (value.length *value[0].length)/(THREAD_COUNT+1);
		int value_rows = value.length;
		int value_cols = value[0].length;

		int min,max;
		for(int id=0; id<THREAD_COUNT; id++)
		{
			min = id*local_step;
			max = (id+1)*local_step;
			int enclosing_values[] = {min,max};
			thread[id] = new Thread(() -> {
				for (int c = enclosing_values[0]; c < enclosing_values[1]; c++) {
					int i = c%value_rows;
					int j = c/value_cols;
					int x_offset = i - value_rows/2;
					int y_offset = j - value_cols/2;
					try {
						set(x+x_offset,y+y_offset,value[i][j]);
					}catch(Exception e) {}
				}
			});
			thread[id].setDaemon(true);
			thread[id].start();
		}

		min = THREAD_COUNT*local_step;
		max = value_rows*value_cols;
		for (int c = min; c < max; c++) {
			int i = c%value_rows;
			int j = c/value_cols;
			int x_offset = i - value_rows/2;
			int y_offset = j - value_cols/2;
			try {
				set(x+x_offset,y+y_offset,value[i][j]);
			}catch(Exception e) {}
		}

		for(int id=0; id<THREAD_COUNT; id++)
		{
			try {
				thread[id].join();
			} catch (InterruptedException e) {
                            System.err.println(e.getMessage());				
			}
		}
	}
        
        /**
         * 
         * @param x init row
         * @param y init col
         * @param x1 end row
         * @param y1 end col
         * @param operator operator
         * @throws AssertionError "(x > x1 || y > y1)"
         */
	public void ranged_foreach(int x, int y,int x1,int y1,Operator<T> operator) {
		if( (x > x1 || y > y1) )
		{
			String msg = "\n> Expected:\t(x < x1 || y < y1)\n> Actual:\t(";
			String format = "%s < %s";
			String format_not = "%s !< %s";
			msg += (x < x1)?String.format(format, x,x1):String.format(format_not, x,x1);
			msg += " || ";
			msg += (y < y1)?String.format(format, y,y1):String.format(format_not, y,y1);
			msg += ")";
			throw new AssertionError(msg);
		}


		int ranged_rows = (x1+1) - x;
		int ranged_cols = (y1+1) - y;
		int ranged_step = (ranged_rows *ranged_rows)/(THREAD_COUNT+1);

		int min,max;
		for(int id=0; id<THREAD_COUNT; id++)
		{
			min = id*ranged_step;
			max = (id+1)*ranged_step;
			int enclosing_values[] = {min,max};
			thread[id] = new Thread(() -> {
				for (int c = enclosing_values[0]; c < enclosing_values[1]; c++) {
					int i = c%ranged_rows + x;
					int j = c/ranged_rows + y;
					T value = operator.operate(i, j, get(i,j));
					set(i,j,value);
				}
			});
			thread[id].setDaemon(true);
			thread[id].start();
		}


		min = THREAD_COUNT*ranged_step;
		max = ranged_rows*ranged_cols;
		for (int c = min; c < max; c++) {
			int i = c%ranged_rows + x;
			int j = c/ranged_rows + y;
			T value = operator.operate(i, j, get(i,j));
			set(i,j,value);
		}

		for(int id=0; id<THREAD_COUNT; id++)
		{
			try {
				thread[id].join();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());	
			}
		}
	}

	public final void foreach(Operator<T> operator) {
		int min,max;
		for(int id=0; id<THREAD_COUNT; id++)
		{
			min = id*step;
			max = (id+1)*step;
			int enclosing_values[] = {min,max};
			thread[id] = new Thread(() -> {
				interal_foreach(enclosing_values[0],enclosing_values[1],operator);
			});
			thread[id].setDaemon(true);
			thread[id].start();
		}

		min = THREAD_COUNT*step;
		max = rows*cols;
		interal_foreach(min,max,operator);

		for(int id=0; id<THREAD_COUNT; id++)
		{
			try {
				thread[id].join();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());	
			}
		}
	}

	private void interal_foreach(int min,int max,Operator<T> operator) {
		for(int i = min; i<max; i++) {
			int x = i%rows;
			int y = i/rows;
			T value = operator.operate(x,y,get(x,y));
			set(x,y,value);
		}
	}

	@Override
	public String toString() {
		String output = " ";
		for (int i = 0; i < cols; i++) {
			output += i+"\t";
		}
		output += "\n";

		for(int i = 0; i<rows ;i++)
		{
			output += "[";
			for(int j=0; j<cols;j++)
			{
				output += data[i][j] + (((j+1)==cols)?"":"\t");
			}
			output += "] "+i+"\n";
		}
		return output;
	}
        
	public T[][] toArray() {return data;}
}

