//  MoSi_ESP.java
package method;

import jmetal.core.*;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal_extended.ArrayArrList;
import jmetal_extended.XArrayList;
import model.BaseStation;
import model.ConstNum;
import model.ESP;
import utils.AlgorithmUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 * This class representing MoSi-ESP algorithm
 */
@SuppressWarnings("serial")
public class MoSi_ESP extends Algorithm{
	
	/**
	 * The maximum constraints of workload
	 */
	private double MAX_WORKTIME;
	
	/**
	 * The maximum constraints of connecting number
	 */
	private double MAX_CONN;

	/**
	 * The number of base stations
	 */
	private int BSNum_;
	
	/**
	 * Stores the info of base stations BaseStation
	 * including longitude, latitude, worktime and the number of connection
	 */
	private BaseStation[] BSList_;

	/**
	 * Stores the info of base station communication network
	 */
	private ArrayList<Integer>[] BSGraph_;

	/**
	 * Stores index of non-serviced base stations
	 */
	private HashSet<Integer> unservicedBSSet_;

	/**
	 * Stores the info of particle mutation
	 */
	private int[][] M1_;
	private int[][] M2_;

	/**
	 * Stores the number of particles_ used
	 */
	private int particlesSize_;

	/**
	 * Stores the maximum size for the archive
	 */
	public int archiveSize_;

	/**
	 * Stores the maximum number of iteration_
	 */
	private int maxIterations_;

	/**
	 * Stores the current number of iteration_
	 */
	private int iteration_;

	/**
	 * Stores the particles
	 */
	private SolutionSet particles_;

	/**
	 * Stores the best_ solutions founds so far for each particles (pbest)
	 */
	private Solution[] best_;

	/**
	 * Stores the leaders_ (gbest)
	 */
	private CrowdingArchive leaders_;

	/**
	 * Stores a comparator for checking dominance
	 */
	private Comparator<Solution> dominance_;

	/**
	 * Stores a comparator for crowding checking
	 */
	private Comparator<Solution> crowdingDistanceComparator_;

	/**
	 * Stores a Distance object
	 */
	private Distance distance_;
	
	/**
	 * Constructor
	 * 
	 * @param problem ESP Problem to solve
	 */
	public MoSi_ESP(Problem problem) {
		super(problem);
	}

	/**
	 * Initialize parameters of the algorithm
	 */
	@SuppressWarnings("unchecked")
	private void initParams() {
		MAX_WORKTIME = ConstNum.MAX_WORKTIME;
		MAX_CONN = ConstNum.MAX_CONN;
		BSNum_ = ConstNum.nBaseStation;
		
		// Get the input parameters
		particlesSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
		archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
		
		// Get base station data
		BSList_ = ((ESP) problem_).getBSList();
		
		// Get the info of base station communication network
		BSGraph_ = ((ESP) problem_).getBSGraph();

		// Initialize the particle population
		particles_ = new SolutionSet(particlesSize_);
		best_ = new Solution[particlesSize_];
		leaders_ = new CrowdingArchive(archiveSize_, problem_.getNumberOfObjectives());

		// Initialize the dominator for equalless and dominance
		dominance_ = new DominanceComparator();
		crowdingDistanceComparator_ = new CrowdingDistanceComparator();
		distance_ = new Distance();

		// Initialize others parameters
		unservicedBSSet_ = new HashSet<Integer>();
		for (int i = 0; i < BSNum_; i++) 
			unservicedBSSet_.add(i);
		M1_ = new int[particlesSize_][BSNum_];
		M2_ = new int[particlesSize_][BSNum_];
		iteration_ = 0;
	} // initParams

	/**
	 * Update the position of each particle
	 * 
	 * @throws JMException
	 */
	private void updatePositions() throws JMException {
		Variable[] bestGlobal;

		for (int i = 0; i < particlesSize_; i++) {
			XArrayList vars = new XArrayList(particles_.get(i));
			Variable[] particle = particles_.get(i).getDecisionVariables();
			Variable[] bestParticle = best_[i].getDecisionVariables();
			
			int index = 0;
			while (index == i) {
				index = PseudoRandom.randInt(0, best_.length - 1);
			}
			Variable[] bestParticle_r = best_[index].getDecisionVariables();

			// Select a global best_ for calculate the speed of particle i, bestGlobal
			Solution one, two, gbest;
			int pos1 = PseudoRandom.randInt(0, leaders_.size() - 1);
			int pos2 = PseudoRandom.randInt(0, leaders_.size() - 1);
			one = leaders_.get(pos1);
			two = leaders_.get(pos2);

			if (crowdingDistanceComparator_.compare(one, two) < 1) {
				bestGlobal = one.getDecisionVariables();
				gbest = one;
			} else {
				bestGlobal = two.getDecisionVariables();
				gbest = two;
			}

			// Params for velocity equation
			SolutionItem[] codeList_rbest = ((ArrayArrList) bestParticle_r[0]).getArray();
			SolutionItem[] codeList_pbest = ((ArrayArrList) bestParticle[0]).getArray();
			SolutionItem[] codeList_gbest = ((ArrayArrList) bestGlobal[0]).getArray();
			SolutionItem[] codeList = ((ArrayArrList) particle[0]).getArray();

			// Update Params
			double[] p = updateParams(i, iteration_, best_[i], best_[index], gbest);

			// Computing the position of this particle
			double rand = PseudoRandom.randDouble(0, 1);
			if (rand < p[0]) {
				updateCodeList(codeList, codeList_pbest);
			} else if (rand >= p[0] && rand < p[0] + p[1]) {
				updateCodeList(codeList, codeList_gbest);
			} else if (rand >= p[0] + p[1] && rand < 1) {
				updateCodeList(codeList, codeList_rbest);
			}

			// Mutation
			if (PseudoRandom.randDouble(0, 1) < 0.5) {
				codeList = Mutation1(codeList, iteration_, i);
			} else {
				codeList = Mutation2(codeList, iteration_, i);
			}

			// Delete the record that violated the constraints
			for (int j = 0; j < codeList.length; j++) {
				ArrayList<Integer> arrayList = codeList[j].getBSList();
				if (arrayList.isEmpty())
					continue;
				else {
					if (!codeList[j].isServer()) {
						arrayList.clear();
					} else {
						for (int k = 1; k < arrayList.size(); k++) {
							if (codeList[arrayList.get(k)].isServer()) {
								arrayList.remove(k);
								k--;
							}
						} // EndFor
					}
				}
			} // EndFor

			// Adjust the solution to be feasible
			codeList = adjustCodeList(codeList);
			vars.setArray(codeList);
		}
	} // updatePositions

	/**
	 * Update current solution based on personal best or global best
	 * @param codeList
	 * @param codeList_best
	 * @return
	 */
	private SolutionItem[] updateCodeList(SolutionItem[] codeList, SolutionItem[] codeList_best) {
		for (int j = 0; j < codeList.length; j++) {
			if (codeList[j].isServer() != codeList_best[j].isServer()) {
				if (!codeList[j].isServer()) {
					codeList[j].getBSList().add(j);
					codeList[j].setServerFlag(true);
				} else {
					codeList[j].getBSList().clear();
					codeList[j].setServerFlag(false);
				}
			}
		}
		return codeList;
	}// updateCodeList

	/***
	 * Update parameters used by iteration
	 * @param iteration_
	 * @param best_p
	 * @param best_r
	 * @param gbest_
	 */
	private double[] updateParams(int particleIndex, int iteration_, Solution best_p, Solution best_r, Solution gbest_) {
		
		double energyCost_Max = best_[0].getObjective(0);
		double energyCost_Min = best_[0].getObjective(0);
		double gini_Max = best_[0].getObjective(1);
		double gini_Min = best_[0].getObjective(1);
		for (int i = 1; i < best_.length; i++) {
			if (best_[i].getObjective(0) < energyCost_Min) {
				energyCost_Min = best_[i].getObjective(0);
			}

			if (best_[i].getObjective(0) > energyCost_Max) {
				energyCost_Max = best_[i].getObjective(0);
			}

			if (best_[i].getObjective(1) < gini_Min) {
				gini_Min = best_[i].getObjective(1);
			}

			if (best_[i].getObjective(1) > gini_Max) {
				gini_Max = best_[i].getObjective(1);
			}
		}

		double aux_p_gini = (gini_Max - best_p.getObjective(1)) / (gini_Max - gini_Min);
		double aux_g_gini = (gini_Max - gbest_.getObjective(1)) / (gini_Max - gini_Min);
		double aux_r_gini = (gini_Max - best_r.getObjective(1)) / (gini_Max - gini_Min);

		double aux_p_energyCost = (energyCost_Max - best_p.getObjective(0)) / (energyCost_Max - energyCost_Min);
		double aux_g_energyCost = (energyCost_Max - gbest_.getObjective(0)) / (energyCost_Max - energyCost_Min);
		double aux_r_energyCost = (energyCost_Max - best_r.getObjective(0)) / (energyCost_Max - energyCost_Min);

		double aux = aux_p_gini + aux_g_gini + aux_r_gini + aux_p_energyCost + aux_g_energyCost + aux_r_energyCost;
		double[] p = new double[3];
		p[0] = (aux_p_gini + aux_p_energyCost) / aux;
		p[1] = (aux_g_gini + aux_g_energyCost) / aux;
		p[2] = (aux_r_gini + aux_r_energyCost) / aux;

		return p;
	}

	private SolutionItem[] Mutation1(SolutionItem[] codeList, int iteration_, int index) {
		for (int i = 0; i < codeList.length; i++) {
			if (!codeList[i].isServer())
				continue;
			if (M1_[index][i] <= iteration_ && PseudoRandom.randDouble(0, 1) < 0.2) {
				codeList[i].getBSList().clear();
				codeList[i].setServerFlag(false);
				M1_[index][i] = iteration_ + 3;
			}
		}
		return codeList;
	}

	private SolutionItem[] Mutation2(SolutionItem[] codeList, int iteration_, int index) {
		for (int i = 0; i < codeList.length; i++) {
			if (codeList[i].isServer())
				continue;
			if (M2_[index][i] <= iteration_ && PseudoRandom.randDouble(0, 1) < 0.2) {
				codeList[i].getBSList().add(i);
				codeList[i].setServerFlag(true);
				M2_[index][i] = iteration_ + 3;
			}
		}
		return codeList;
	}

	/***
	 * Repair codeList of Solution
	 * @param codeList
	 * @return codeList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SolutionItem[] adjustCodeList(SolutionItem[] codeList) {
		
		// Stores the info whether the base station is served by the server
		boolean[] isServiced = new boolean[BSNum_];
		// Stores the index of unservice base station nearby the base station
		ArrayList[] unservicedNearBSList = new ArrayList[BSNum_];
		
		// Reset the unservicedBSSet_
		unservicedBSSet_.clear();
		for (int j = 0; j < BSNum_; j++) {
			unservicedBSSet_.add(j);
		}

		// Record the info of unservicedBSSet_
		for (int j = 0; j < codeList.length; j++) {
			for (Integer temp : codeList[j].getBSList()) {
				unservicedBSSet_.remove(temp);
				isServiced[temp] = true;
			}
		}
		
		for (int j = 0; j < BSNum_; j++) {
			unservicedNearBSList[j] = new ArrayList<Integer>();
			for (Integer index : BSGraph_[j]) {
				if (!isServiced[index]) {
					unservicedNearBSList[j].add(index);
				}
			}
		}
		
		// Store the usage information about server resources
		int[][] resInfo = AlgorithmUtils.updateResInfo(codeList, BSList_);
		
		// Store a set of the unserviced base station indexs as ArrayList. 
		ArrayList<ArrayList<Integer>> unservicedBSIndexs = new ArrayList<ArrayList<Integer>>(); 
		// Store the number of available servers of each ArrayList on the unservicedBSIndexs.
		ArrayList<Integer> serversNums = new ArrayList<Integer>();
		
		// 1. Divide the index of unserviced base station to several ArrayList 
		//    according to the number of available servers nearby it. 
		//    (Insertion sort, sorted by the number of available servers)
		for (Integer BSIndex : unservicedBSSet_) {
			// Get the BaseStation object whose index is BSIndex.
			BaseStation bs = BSList_[BSIndex];
			// Store number of available servers.
			int num = 0;
			// Get the neighbor base station index of BSIndex base station.
			ArrayList<Integer> nearBS = BSGraph_[BSIndex];
			// Calculate the number of available servers for the BSIndex base station.
			for (int j = 0; j < nearBS.size(); j++) {
				int tmpIndex = nearBS.get(j);
				// whether the tmpIndex base station has deployed servers and has sufficient service resources.
				if (codeList[tmpIndex].isServer()){
					if (resInfo[0][tmpIndex] + bs.getWorktime() <= MAX_WORKTIME
							&& resInfo[1][tmpIndex] + bs.getConn() <= MAX_CONN * 0.9) {
						num++;
					}
				}
			}

			// whether the unservicedBSIndexs is empty or the num greater than the maximum within serversNums.
			if (unservicedBSIndexs.isEmpty() || serversNums.get(serversNums.size() - 1) < num) {
				// Append the num to the end of serversNums, 
				// and the corresponding index subset of base stations to the end of unservicedBSIndexs.
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp.add(BSIndex);
				unservicedBSIndexs.add(temp);
				serversNums.add(num);
			} else {
				for (int j = 0; j < serversNums.size(); j++) {
					// Search for the number of avaiable servers which is equal to num sequentially.
					if (serversNums.get(j) > num) {
						// If it does not exist, 
						// a new num value and its corresponding index subset of base stations are inserted.
						ArrayList<Integer> temp = new ArrayList<Integer>();
						temp.add(BSIndex);
						unservicedBSIndexs.add(j, temp);
						serversNums.add(j, num);
						break;
					} else if (serversNums.get(j) == num) {
						// If it exists, the base station indexs subset is inserted directly.
						unservicedBSIndexs.get(j).add(BSIndex);
						break;
					}
				}
			}
		}
		unservicedBSSet_.clear();

		// 2. Unserved base stations are assigned to edge servers or deploy a new edge server.
		for (int i = 0; i < unservicedBSIndexs.size(); i++) {
			// A base station with a small number of feasible servers is preferred.
			while (!unservicedBSIndexs.get(i).isEmpty()) {
				// Find out a (set of) base station index with the largest degree
				int maxBSNum = -1;
				ArrayList<Integer> array = new ArrayList<>();
				for (int nearIndex : unservicedBSIndexs.get(i)) {
					if (maxBSNum == -1 || unservicedNearBSList[nearIndex].size() > maxBSNum) {
						array.clear();
						array.add(nearIndex);
						maxBSNum = unservicedNearBSList[nearIndex].size();
					} else if (unservicedNearBSList[nearIndex].size() == maxBSNum) {
						array.add(nearIndex);
					}
				}
				
				// A base station is randomly selected from the array and assigned to serve it.
				int randomIndex = array.get((int) (Math.random() * array.size()));
				codeList = allocBaseStation(randomIndex, codeList, resInfo);
				
				// Delete the base station index from the unservedBSIndexs.
				unservicedBSIndexs.get(i).remove((Integer) randomIndex);
				
				// Update unservicedNearBSList
				for (Integer temp : (ArrayList<Integer>)unservicedNearBSList[randomIndex]) {
					unservicedNearBSList[temp].remove((Integer) randomIndex);
				}
			}
		}		
		return codeList;
	}

	/***
	 * Build the communication between the unserviced base station and the server
	 * @param unservicedBSIndexs
	 * @param codeList
	 * @return
	 */
	private SolutionItem[] allocBaseStation(Integer BSIndex, SolutionItem[] codeList, int[][] resInfo) {
		int serverIndex = -1;
		// Search for a server that satisfies the constraint and has sufficient resources
		ArrayList<Integer> nearBS = BSGraph_[BSIndex];
		// Search in the neighbor base station
		for (int k = 0; k < nearBS.size(); k++) {
			int tmpIndex = nearBS.get(k);
			// whether the base station deploys edge server
			if (codeList[tmpIndex].isServer()) {
				// whether the remain service resource of current server is sufficient
				if (resInfo[0][tmpIndex] + BSList_[BSIndex].getWorktime() <= MAX_WORKTIME
						&& resInfo[1][tmpIndex] + BSList_[BSIndex].getConn() <= MAX_CONN * 0.9) {
					// whether the the remain service resource of current server 
					// is greater than that of serverIndex corresponds to the server.
					if (serverIndex == -1 ||
						resInfo[1][serverIndex] * resInfo[0][serverIndex] > resInfo[1][tmpIndex] * resInfo[0][tmpIndex]) {
						serverIndex = tmpIndex;
					}
				}
			}
		}
		
		if (serverIndex != -1) {
			// If exist one available server, build the service relationship between server and base station.
			codeList[serverIndex].getBSList().add(BSIndex);
			resInfo[0][serverIndex] += BSList_[BSIndex].getWorktime();
			resInfo[1][serverIndex] += BSList_[BSIndex].getConn();
		} else {
			// If no server is available 
			codeList[BSIndex].getBSList().add(BSIndex);// Deploy a new server at the base station.
			codeList[BSIndex].setServerFlag(true);
			resInfo[0][BSIndex] = BSList_[BSIndex].getWorktime();
			resInfo[1][BSIndex] = BSList_[BSIndex].getConn();
		}
		return codeList;
	}
	
	/**
	 * Runs of the MoSi-ESP algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 *         as a result of the algorithm execution
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		// ->Step 1. Creates and initializes a particle population
		initParams();
		for (int i = 0; i < particlesSize_; i++) {
			double[][] resInfo = new double[2][BSNum_];
			SolutionItem[] codeList = new SolutionItem[BSNum_];
			unservicedBSSet_.clear();
			for (int j = 0; j < codeList.length; j++) {
				codeList[j] = new SolutionItem();
				unservicedBSSet_.add(j);
			}

			Solution particle = new Solution(problem_);
			XArrayList vars = new XArrayList(particle);

			while (!unservicedBSSet_.isEmpty()) {
				int index = (int) (Math.random() * codeList.length);
				if (unservicedBSSet_.contains(index)) {// Base station without network service
					codeList[index].getBSList().add(index);
					codeList[index].setServerFlag(true);// Deploy a new edge server on this base station
					unservicedBSSet_.remove(index);
					resInfo[0][index] = BSList_[index].getWorktime();
					resInfo[1][index] = BSList_[index].getConn();
				} else
					continue;

				// Build the communication connection between the server and the base station
				ArrayList<Integer> nearBS = BSGraph_[index];
				for (int j = 0; j < nearBS.size(); j++) {
					int bsIndex = nearBS.get(j);
					if (unservicedBSSet_.contains(bsIndex)) {
						if (resInfo[0][index] + BSList_[bsIndex].getWorktime() <= 21600
								&& resInfo[1][index] + BSList_[bsIndex].getConn() <= 50 * 0.9) {//
							codeList[index].getBSList().add(bsIndex);
							unservicedBSSet_.remove(bsIndex);
							resInfo[0][index] += BSList_[bsIndex].getWorktime();
							resInfo[1][index] += BSList_[bsIndex].getConn();
						}
					}
				}
			} // whileEnd

			vars.setArray(codeList);
			problem_.evaluate(particle);
			particles_.add(particle);
		}

		// -> Step 2. Initialize the best position of each particles and leaders
		for (int i = 0; i < particlesSize_; i++) {
			Solution particle = deepCopy(particles_.get(i));
			best_[i] = particle;
			leaders_.add(particle);
		}
		
		// Update distance of solution set
		distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());
		
		// -> Step 3. iteration
		while (iteration_ < maxIterations_) {
//			System.out.println("Iterations:" + (iteration_ + 1));

			// Update positions of the particles_
			updatePositions();
			
			// Evaluate the new particles_ in new positions
			for (int i = 0; i < particlesSize_; i++) {
				Solution particle = particles_.get(i);
				problem_.evaluate(particle);
			}

			// Actualize the archive
			for (int i = 0; i < particlesSize_; i++) {
				Solution particle = deepCopy(particles_.get(i));
				leaders_.add(particle);
			}

			// Actualize the memory of this particle
			for (int i = 0; i < particlesSize_; i++) {
				int flag = dominance_.compare(particles_.get(i), best_[i]);
				if (flag != 1) { // the new particle is best_ than the older remember
					Solution particle = deepCopy(particles_.get(i));
					best_[i] = particle;
				}
			}

			// Update distance of leader_
			distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());
			iteration_++;
		}
		
		return this.leaders_;
	} // execute
	
		
	private Solution deepCopy(Solution solution) throws JMException, ClassNotFoundException {
		Solution temp = new Solution(problem_);
		XArrayList vars = new XArrayList(temp);
		vars.setArray(((ArrayArrList) solution.getDecisionVariables()[0]).getArray());
		problem_.evaluate(temp);
		return temp;
	}


} // MoSi_ESP
