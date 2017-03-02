import javax.swing.*;
import java.io.IOException;
import java.io.RandomAccessFile;

class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;

	// Create new file
	void createFile(String fileName) {
		RandomAccessFile file = null;

		try // open file for reading and writing
		{
			file = new RandomAccessFile(fileName, "rw");

		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error processing file!");
			System.exit(1);
		} // end catch

		finally {
			closeFile();
		} // end finally
	} // end createFile

	// Open file for adding or changing records
	void openWriteFile(String fileName) {
		try // open file
		{
			output = new RandomAccessFile(fileName, "rw");
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		} // end catch
	} // end method openFile

	// Close file for adding or changing records
	void closeFile() {
		try // close file and exit
		{
			if (output != null)
				output.close();
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		} // end catch
	} // end closeFile

	// Add records to file
	long addRecords(Employee employeeToAdd) {
		long currentRecordStart = 0;

		// object to be written to file
		RandomAccessEmployeeRecord record;

		try // output values to file
		{
			record = new RandomAccessEmployeeRecord(employeeToAdd.getEmployeeId(), employeeToAdd.getPps(),
					employeeToAdd.getSurname(), employeeToAdd.getFirstName(), employeeToAdd.getGender(),
					employeeToAdd.getDepartment(), employeeToAdd.getSalary(), employeeToAdd.getFullTime());

			output.seek(output.length());// Look for proper position
			record.write(output);// Write object to file
			currentRecordStart = output.length();
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} // end catch

		return currentRecordStart - RandomAccessEmployeeRecord.SIZE;// Return
		// position
		// where
		// object
		// starts in
		// the file
	}// end addRecords

	// Change details for existing object
	void changeRecords(Employee newDetails, long byteToStart) {
		// object to be written to file
		RandomAccessEmployeeRecord record;
		try // output values to file
		{
			record = new RandomAccessEmployeeRecord(newDetails.getEmployeeId(), newDetails.getPps(),
					newDetails.getSurname(), newDetails.getFirstName(), newDetails.getGender(),
					newDetails.getDepartment(), newDetails.getSalary(), newDetails.getFullTime());

			output.seek(byteToStart);// Look for proper position
			record.write(output);// Write object to file
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} // end catch
	}// end changeRecors

	// Delete existing object
	void deleteRecords(long byteToStart) {

		// object to be written to file
		RandomAccessEmployeeRecord record;

		try // output values to file
		{
			record = new RandomAccessEmployeeRecord();// Create empty object
			output.seek(byteToStart);// Look for proper position
			record.write(output);// Replace existing object with empty object
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} // end catch
	}// end deleteRecords

	// Open file for reading
	void openReadFile(String fileName) {
		try // open file
		{
			input = new RandomAccessFile(fileName, "r");
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not suported!");
		} // end catch
	} // end method openFile

	// Get position of first record in file
	long getFirst() {
		long byteToStart = 0;

		try {// try to get file
			input.length();
		} // end try
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}// end catch

		return byteToStart;
	}// end getFirst

	// Get position of last record in file
	long getLast() {
		long byteToStart = 0;

		try {// try to get position of last record
			byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
		}// end try 
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}// end catch

		return byteToStart;
	}// end getFirst

	// Get position of next record in file
	long getNext(long readFrom) {
		long byteToStart = readFrom;

		try {// try to read from file
			input.seek(byteToStart);// Look for proper position in file
			// if next position is end of file go to start of file, else get next position
			if (byteToStart + RandomAccessEmployeeRecord.SIZE == input.length())
				byteToStart = 0;
			else
				byteToStart = byteToStart + RandomAccessEmployeeRecord.SIZE;
		} // end try
		catch (NumberFormatException | IOException e) {
			System.out.println("Error " + e.getMessage());
		} // end catch
		// end catch
		return byteToStart;
	}// end getFirst

	// Get position of previous record in file
	long getPrevious(long readFrom) {
		long byteToStart = readFrom;

		try {// try to read from file
			input.seek(byteToStart);// Look for proper position in file
			// if previous position is start of file go to end of file, else get previous position
			if (byteToStart == 0)
				byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
			else
				byteToStart = byteToStart - RandomAccessEmployeeRecord.SIZE;
		} // end try
		catch (NumberFormatException | IOException e) {
			System.out.println("Error " + e.getMessage());

		} // end catch
		// end catch
		return byteToStart;
	}// end getPrevious

	// Get object from file in specified position
	Employee readRecords(long byteToStart) {
		Employee thisEmp;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read file and get record
			input.seek(byteToStart);// Look for proper position in file
			record.read(input);// Read record from file
		} // end try
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}// end catch

		thisEmp = record;

		return thisEmp;
	}// end readRecords

	// Check if PPS Number already in use
	boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		long currentByte = 0;

		try {// try to read from file and look for PPS Number
			// Start from start of file and loop until PPS Number is found or search returned to start position
			while (currentByte != input.length() && !ppsExist) {
				//if PPS Number is in position of current object - skip comparison
				if (currentByte != currentByteStart) {
					input.seek(currentByte);// Look for proper position in file
					record.read(input);// Get record from file
					// If PPS Number already exist in other record display message and stop search
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}// end if
				}// end if
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}// end while
		} // end try
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}// end catch

		return ppsExist;
	}// end isPpsExist

	// Check if any record contains valid ID - greater than 0
	boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentByte = 0;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read from file and look for ID
			// Start from start of file and loop until valid ID is found or search returned to start position
			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);// Look for proper position in file
				record.read(input);// Get record from file
				// If valid ID exist in stop search
				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}// end while
		}// end try
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}// end catch

		return someoneToDisplay;
	}// end isSomeoneToDisplay
}// end class RandomFile
