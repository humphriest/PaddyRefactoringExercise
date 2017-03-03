import javax.swing.*;
import java.io.IOException;
import java.io.RandomAccessFile;

class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;

	void createFile(String fileName) {
		RandomAccessFile file = null;
		try
		{
			file = new RandomAccessFile(fileName, "rw");

		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error processing file!");
			System.exit(1);
		} // end catch

		finally {
			closeFile();
		}
	}

	void openWriteFile(String fileName) {
		try
		{
			output = new RandomAccessFile(fileName, "rw");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		}
	}

	void closeFile() {
		try
		{
			if (output != null)
				output.close();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		}
	}

	long addRecords(Employee employeeToAdd) {
		long currentRecordStart = 0;

		RandomAccessEmployeeRecord record;

		try
		{
			record = new RandomAccessEmployeeRecord(employeeToAdd.getEmployeeId(), employeeToAdd.getPps(),
					employeeToAdd.getSurname(), employeeToAdd.getFirstName(), employeeToAdd.getGender(),
					employeeToAdd.getDepartment(), employeeToAdd.getSalary(), employeeToAdd.getFullTime());

			output.seek(output.length());
			record.write(output);
			currentRecordStart = output.length();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}

		return currentRecordStart - RandomAccessEmployeeRecord.SIZE;
	}

	void changeRecords(Employee newDetails, long byteToStart) {
		RandomAccessEmployeeRecord record;
		try
		{
			record = new RandomAccessEmployeeRecord(newDetails.getEmployeeId(), newDetails.getPps(),
					newDetails.getSurname(), newDetails.getFirstName(), newDetails.getGender(),
					newDetails.getDepartment(), newDetails.getSalary(), newDetails.getFullTime());

			output.seek(byteToStart);
			record.write(output);
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}
	}

	void deleteRecords(long byteToStart) {
		RandomAccessEmployeeRecord record;

		try
		{
			record = new RandomAccessEmployeeRecord();
			output.seek(byteToStart);
			record.write(output);
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}
	}

	void openReadFile(String fileName) {
		try
		{
			input = new RandomAccessFile(fileName, "r");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not suported!");
		}
	}

	long getFirst() {
		long byteToStart = 0;

		try {
			input.length();
		}
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}

		return byteToStart;
	}

	long getLast() {
		long byteToStart = 0;

		try {
			byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
		}
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}

		return byteToStart;
	}

	long getNext(long readFrom) {
		long byteToStart = readFrom;

		try {
			input.seek(byteToStart);
			if (byteToStart + RandomAccessEmployeeRecord.SIZE == input.length())
				byteToStart = 0;
			else
				byteToStart = byteToStart + RandomAccessEmployeeRecord.SIZE;
		}
		catch (NumberFormatException | IOException e) {
			System.out.println("Error " + e.getMessage());
		}
		return byteToStart;
	}

	long getPrevious(long readFrom) {
		long byteToStart = readFrom;

		try {
			input.seek(byteToStart);
			if (byteToStart == 0)
				byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
			else
				byteToStart = byteToStart - RandomAccessEmployeeRecord.SIZE;
		}
		catch (NumberFormatException | IOException e) {
			System.out.println("Error " + e.getMessage());

		}
		return byteToStart;
	}

	Employee readRecords(long byteToStart) {
		Employee thisEmp;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			input.seek(byteToStart);
			record.read(input);
		}
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}
		thisEmp = record;
		return thisEmp;
	}

	boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		long currentByte = 0;

		try {
			while (currentByte != input.length() && !ppsExist) {
				if (currentByte != currentByteStart) {
					input.seek(currentByte);
					record.read(input);
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}
				}
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}
		return ppsExist;
	}

	boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentByte = 0;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);
				record.read(input);
				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
		}

		return someoneToDisplay;
	}
}
