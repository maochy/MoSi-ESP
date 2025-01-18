package jmetal_extended;

import java.util.ArrayList;

import jmetal.core.Problem;
import jmetal.core.Variable;
import jmetal.util.JMException;
import method.SolutionItem;

/**
 * Class implementing a decision encodings.variable representing an array of SolutionItem
 */
@SuppressWarnings("serial")
public class ArrayArrList extends Variable {
	/**
	 * Problem using the type
	 */
	private Problem problem_;

	/**
	 * Stores an array of SolutionItem
	 */
//	public ArrayList<Integer>[] array_;
	public SolutionItem[] array_;

	/**
	 * Stores the length of the array
	 */
	private int size_;

	/**
	 * Store the lower and upper bounds of each int value of the array in case of
	 * having each one different limits
	 */
	private int[] lowerBounds_;
	private int[] upperBounds_;

	/**
	 * Constructor
	 */
	public ArrayArrList() {
		lowerBounds_ = null;
		upperBounds_ = null;
		size_ = 0;
		array_ = null;
		problem_ = null;
	} // Constructor

	/**
	 * Constructor
	 * 
	 * @param size Size of the SolutionItem
	 */
	public ArrayArrList(int size) {
		size_ = size;
		array_ = new SolutionItem[size_];
		lowerBounds_ = new int[problem_.getNumberOfVariables()];
		upperBounds_ = new int[problem_.getNumberOfVariables()];
	} // Constructor

	/**
	 * Constructor
	 * 
	 * @param size Size of the SolutionItem
	 */
	public ArrayArrList(int size, Problem problem) {
		problem_ = problem;
		size_ = size;
		array_ = new SolutionItem[size_];
		lowerBounds_ = new int[problem_.getNumberOfVariables()];
		upperBounds_ = new int[problem_.getNumberOfVariables()];

		for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
			lowerBounds_[i] = (int) problem_.getLowerLimit(i);
			upperBounds_[i] = (int) problem_.getUpperLimit(i);
		}
	} // Constructor

	/**
	 * Constructor
	 * 
	 * @param size        The size of the SolutionItem
	 * @param lowerBounds Lower bounds
	 * @param upperBounds Upper bounds
	 */
	public ArrayArrList(int size, int[] lowerBounds, int[] upperBounds) {
		size_ = size;
		array_ = new SolutionItem[size_];

		lowerBounds_ = new int[problem_.getNumberOfVariables()];
		upperBounds_ = new int[problem_.getNumberOfVariables()];

		for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
			lowerBounds_[i] = lowerBounds[i];
			upperBounds_[i] = upperBounds[i];
		}
	} // Constructor

	/**
	 * Copy Constructor
	 * 
	 * @param arrayAL The arrayArrList to copy
	 */
	private ArrayArrList(ArrayArrList arrayAL) {
		size_ = arrayAL.size_;
		array_ = new SolutionItem[size_];

		lowerBounds_ = new int[arrayAL.lowerBounds_.length];
		upperBounds_ = new int[arrayAL.upperBounds_.length];
		for (int i = 0; i < arrayAL.lowerBounds_.length; i++) {
			lowerBounds_[i] = arrayAL.lowerBounds_[i];
			upperBounds_[i] = arrayAL.upperBounds_[i];
		}

		for (int i = 0; i < size_; i++) {
			array_[i] = arrayAL.array_[i];
		} // for
	} // Copy Constructor

	@Override
	public Variable deepCopy() {
		return new ArrayArrList(this);
	} // deepCopy

	/**
	 * Returns the length of the arrayArrList.
	 * 
	 * @return The length
	 */
	public int getLength() {
		return size_;
	} // getLength

	public SolutionItem[] getArray() throws JMException {
		return array_;
	}

	public void setArray(SolutionItem[] array_) throws JMException {
		SolutionItem[] temp = new SolutionItem[size_];
		ArrayList<Integer> arrayList; 
		for (int i = 0; i < temp.length; i++) {
			temp[i] = new SolutionItem();
			arrayList = new ArrayList<Integer>();
			for(Integer index : array_[i].getBSList()) {
				arrayList.add(index);
			}
			temp[i].setBSList(arrayList);
			temp[i].setServerFlag(array_[i].isServer());
		}
		this.array_ = temp;
	}

	/**
	 * Returns a string representing the object
	 * @return The string
	 */
	public String toString() {
		String string = "";
		for (int i = 0; i < size_; i++) {
			ArrayList<Integer> bsList = array_[i].getBSList();
			if (array_[i] == null || bsList.isEmpty()) {
				continue;
			}else {
				String tmp = "";
				for (int j = 0; j < bsList.size(); j++) {
					tmp += bsList.get(j) + ",";
				}
				string += i + ",," + tmp;
			}
		}
		return string;
	} // toString
} // ArrayArrList
