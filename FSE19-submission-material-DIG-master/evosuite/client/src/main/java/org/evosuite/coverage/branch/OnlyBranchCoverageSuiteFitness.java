/**
 * Copyright (C) 2010-2016 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.coverage.branch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.archive.TestsArchive;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fitness function for a whole test suite for all branches
 * 
 * @author Gordon Fraser, Jose Miguel Rojas
 */
public class OnlyBranchCoverageSuiteFitness extends TestSuiteFitnessFunction {

	private static final long serialVersionUID = 2991632394620406243L;

	private final static Logger logger = LoggerFactory.getLogger(TestSuiteFitnessFunction.class);

	// Coverage targets
	public int totalBranches;
	public int totalGoals;
	private final Set<Integer> branchesId;
	
	// Some stuff for debug output
	public int maxCoveredBranches = 0;
	public double bestFitness = Double.MAX_VALUE;

	// Each test gets a set of distinct covered goals, these are mapped by branch id
	private final Map<Integer, TestFitnessFunction> branchCoverageTrueMap = new HashMap<Integer, TestFitnessFunction>();
	private final Map<Integer, TestFitnessFunction> branchCoverageFalseMap = new HashMap<Integer, TestFitnessFunction>();

	private final Set<Integer> toRemoveBranchesT = new HashSet<>();
	private final Set<Integer> toRemoveBranchesF = new HashSet<>();
	
	private final Set<Integer> removedBranchesT = new HashSet<>();
	private final Set<Integer> removedBranchesF = new HashSet<>();
	
	
	private static boolean stopSearch = false;
	
	
	
	/**
	 * <p>
	 * Constructor for BranchCoverageSuiteFitness.
	 * </p>
	 */
	public OnlyBranchCoverageSuiteFitness() {

		String prefix = Properties.TARGET_CLASS_PREFIX;
		
		if (prefix.isEmpty()) {
			prefix = Properties.TARGET_CLASS;
			totalBranches = BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getBranchCountForPrefix(prefix);
		} else {
			totalBranches = BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getBranchCountForPrefix(prefix);
		}
		branchesId = new HashSet<>();

		totalGoals = 2 * totalBranches;

		logger.info("Total branch coverage goals: " + totalGoals);
		logger.info("Total branches: " + totalBranches);

		determineCoverageGoals();
	}


	/**
	 * Initialize the set of known coverage goals
	 */
	private void determineCoverageGoals() {
		List<OnlyBranchCoverageTestFitness> goals = new OnlyBranchCoverageFactory().getCoverageGoals();
		for (OnlyBranchCoverageTestFitness goal : goals) {
			if(Properties.TEST_ARCHIVE)
				TestsArchive.instance.addGoalToCover(this, goal);

			branchesId.add(goal.getBranch().getActualBranchId());
			if (goal.getBranchExpressionValue())
				branchCoverageTrueMap.put(goal.getBranch().getActualBranchId(), goal);
			else
				branchCoverageFalseMap.put(goal.getBranch().getActualBranchId(), goal);
		}
	}

	
	/**
	 * Iterate over all execution results and summarize statistics
	 * 
	 * @param results
	 * @param predicateCount
	 * @param callCount
	 * @param trueDistance
	 * @param falseDistance
	 * @return
	 */
	private boolean analyzeTraces( AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite, List<ExecutionResult> results,
	        Map<Integer, Integer> predicateCount, 
	        Map<Integer, Double> trueDistance, Map<Integer, Double> falseDistance) {
		
		boolean hasTimeoutOrTestException = false;
		for (ExecutionResult result : results) {
			if (result.hasTimeout() || result.hasTestException()) {			
				hasTimeoutOrTestException = true;
				continue;
			}
			
			for (Entry<Integer, Integer> entry : result.getTrace().getPredicateExecutionCount().entrySet()) {
				if (!branchesId.contains(entry.getKey())
						|| (removedBranchesT.contains(entry.getKey())
						&& removedBranchesF.contains(entry.getKey())))
					continue;
				if (!predicateCount.containsKey(entry.getKey()))
					predicateCount.put(entry.getKey(), entry.getValue());
				else {
					predicateCount.put(entry.getKey(),
							predicateCount.get(entry.getKey())
							+ entry.getValue());
				}
			}
			for (Entry<Integer, Double> entry : result.getTrace().getTrueDistances().entrySet()) {
				if(!branchesId.contains(entry.getKey())||removedBranchesT.contains(entry.getKey())) continue;
				if (!trueDistance.containsKey(entry.getKey()))
					trueDistance.put(entry.getKey(), entry.getValue());
				else {
					trueDistance.put(entry.getKey(),
							Math.min(trueDistance.get(entry.getKey()),
									entry.getValue()));
				}
				if ((Double.compare(entry.getValue(), 0.0) ==0)) {
					result.test.addCoveredGoal(branchCoverageTrueMap.get(entry.getKey()));
					if(Properties.TEST_ARCHIVE) {
						TestsArchive.instance.putTest(this, branchCoverageTrueMap.get(entry.getKey()), result);
						toRemoveBranchesT.add(entry.getKey());
						suite.isToBeUpdated(true);
					}
				}
			}
			for (Entry<Integer, Double> entry : result.getTrace().getFalseDistances().entrySet()) {
				if(!branchesId.contains(entry.getKey())||removedBranchesF.contains(entry.getKey())) continue;
				if (!falseDistance.containsKey(entry.getKey()))
					falseDistance.put(entry.getKey(), entry.getValue());
				else {
					falseDistance.put(entry.getKey(),
							Math.min(falseDistance.get(entry.getKey()),
									entry.getValue()));
				}
				if ((Double.compare(entry.getValue(), 0.0) ==0)) {
					result.test.addCoveredGoal(branchCoverageFalseMap.get(entry.getKey()));
					if(Properties.TEST_ARCHIVE) {
						TestsArchive.instance.putTest(this, branchCoverageFalseMap.get(entry.getKey()), result);
						toRemoveBranchesF.add(entry.getKey());
						suite.isToBeUpdated(true);
					}
				}
			}
		}
		return hasTimeoutOrTestException;
	}
	
	@Override
	public boolean updateCoveredGoals() {
		
		if(!Properties.TEST_ARCHIVE)
			return false;
		
		for (Integer branch : toRemoveBranchesT) {
			TestFitnessFunction f = branchCoverageTrueMap.remove(branch);
			if (f != null) {
				removedBranchesT.add(branch);
				if (removedBranchesF.contains(branch)) {
					totalBranches--;
					//if(isFullyCovered(f.getTargetClass(), f.getTargetMethod())) {
					//	removeTestCall(f.getTargetClass(), f.getTargetMethod());
					//}
				}
			} else {
				throw new IllegalStateException("goal to remove not found");
			}
		}
		for (Integer branch : toRemoveBranchesF) {
			TestFitnessFunction f = branchCoverageFalseMap.remove(branch);
			if (f != null) {
				removedBranchesF.add(branch);
				if (removedBranchesT.contains(branch)) {
					totalBranches--;
					//if(isFullyCovered(f.getTargetClass(), f.getTargetMethod())) {
					//	removeTestCall(f.getTargetClass(), f.getTargetMethod());
					//}
				}
			} else {
				throw new IllegalStateException("goal to remove not found");
			}
		}
		
		toRemoveBranchesF.clear();
		toRemoveBranchesT.clear();
		logger.info("Current state of archive: "+TestsArchive.instance.toString());
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Execute all tests and count covered branches
	 */
	@Override
	public double getFitness(
	        AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite) {
		logger.trace("Calculating branch fitness");
		double fitness = 0.0;

		List<ExecutionResult> results = runTestSuite(suite);
		Map<Integer, Double> trueDistance = new HashMap<Integer, Double>();
		Map<Integer, Double> falseDistance = new HashMap<Integer, Double>();
		Map<Integer, Integer> predicateCount = new HashMap<Integer, Integer>();

		// Collect stats in the traces 
		boolean hasTimeoutOrTestException = analyzeTraces(suite, results, predicateCount,
		                                                  trueDistance,
		                                                  falseDistance);

		// Collect branch distances of covered branches
		int numCoveredBranches = 0;

		for (Integer key : predicateCount.keySet()) {
			
			double df = 0.0;
			double dt = 0.0;
			int numExecuted = predicateCount.get(key);
			
			String toString = "true branch -> ";
			TestFitnessFunction trueBranch = branchCoverageTrueMap.get(key);
			if(trueBranch == null) toString = toString + "removed ";
			else toString = toString + trueBranch;
			toString = toString + ";; false branch -> ";
			TestFitnessFunction falseBranch = branchCoverageFalseMap.get(key);
			if(falseBranch == null) toString = toString + "removed ";
			else toString = toString + falseBranch;
			logger.debug("££branchId: " + key + " details: " + toString);
			
			if(removedBranchesT.contains(key))
				numExecuted++;
			if(removedBranchesF.contains(key))
				numExecuted++;
			
			if (trueDistance.containsKey(key)) {
				dt =  trueDistance.get(key);
			}
			if(falseDistance.containsKey(key)){
				df = falseDistance.get(key);
			}
			// If the branch predicate was only executed once, then add 1 
			if (numExecuted == 1) {
				fitness += 1.0;
			} else {
				fitness += normalize(df) + normalize(dt);
			}

			if (falseDistance.containsKey(key)&&(Double.compare(df, 0.0) == 0))
				numCoveredBranches++;

			if (trueDistance.containsKey(key)&&(Double.compare(dt, 0.0) == 0))
				numCoveredBranches++;
		}
		
		if(Properties.STOP_ON_TRUE && !Properties.CUSTOM_MOSA){
			if(branchCoverageFalseMap.isEmpty()){
				//we covered all false branches
				logger.debug("--------We covered all false branches: we should stop the search!");
				stopSearch = true;
				if(branchCoverageTrueMap.isEmpty()){
					logger.debug("--------True branches all covered!");
				}else{
					logger.debug("--------Some true branch is not covered yet. How many? " + branchCoverageTrueMap.size());
				}
			}else{
				logger.debug("--------We didn't cover yet all false branches: search continues to cover remaining " + branchCoverageFalseMap.size() + " branches");
				Set<Integer> keys = branchCoverageFalseMap.keySet();
				for(Integer key: keys){
					logger.debug("False branch uncovered: Key {}, Value {}",key,branchCoverageFalseMap.get(key));
				}
				if(branchCoverageTrueMap.isEmpty()){
					logger.debug("--------True branches all covered!");
				}else{
					logger.debug("--------Some true branch is not covered yet. How many? " + branchCoverageTrueMap.size());
				}
			}
		}
		
		if(Properties.STOP_ON_TRUE && stopSearch && !Properties.CUSTOM_MOSA){
			fitness = 0.0;
			
			printStatusMessages(suite, numCoveredBranches, fitness);
			
			// Calculate coverage
			int coverage = numCoveredBranches;

			coverage +=removedBranchesF.size();
			coverage +=removedBranchesT.size();	
	 		
			if (totalGoals > 0)
				suite.setCoverage(this, (double) coverage / (double) totalGoals);
	        else
	            suite.setCoverage(this, 1);

			suite.setNumOfCoveredGoals(this, coverage);
			suite.setNumOfNotCoveredGoals(this, totalGoals-coverage);
			
			if (hasTimeoutOrTestException) {
				logger.info("Test suite has timed out, setting fitness to max value "
				        + (totalBranches * 2));
				fitness = totalBranches * 2;
				//suite.setCoverage(0.0);
			}
			
			logger.info("&&&&Fitness: " + fitness + " coverage: " + coverage);

			updateIndividual(this, suite, fitness);
			
			return fitness;
		}
		
		// +1 for every branch that was not executed
		fitness += 2 * (totalBranches - predicateCount.size());

		printStatusMessages(suite, numCoveredBranches, fitness);

		// Calculate coverage
		int coverage = numCoveredBranches;

		coverage +=removedBranchesF.size();
		coverage +=removedBranchesT.size();	
 		
		if (totalGoals > 0)
			suite.setCoverage(this, (double) coverage / (double) totalGoals);
        else
            suite.setCoverage(this, 1);

		suite.setNumOfCoveredGoals(this, coverage);
		suite.setNumOfNotCoveredGoals(this, totalGoals-coverage);
		
		if (hasTimeoutOrTestException) {
			logger.info("Test suite has timed out, setting fitness to max value "
			        + (totalBranches * 2));
			fitness = totalBranches * 2;
			//suite.setCoverage(0.0);
		}
		
		logger.info("&&&&Fitness: " + fitness + " coverage: " + coverage);

		updateIndividual(this, suite, fitness);

		assert (coverage <= totalGoals) : "Covered " + coverage + " vs total goals "
		        + totalGoals;
		assert (fitness >= 0.0);
		assert (fitness != 0.0 || coverage == totalGoals) : "Fitness: " + fitness + ", "
		        + "coverage: " + coverage + "/" + totalGoals;
		assert (suite.getCoverage(this) <= 1.0) && (suite.getCoverage(this) >= 0.0) : "Wrong coverage value "
		        + suite.getCoverage(this); 
		
		return fitness;
	}

	/**
	 * Some useful debug information
	 * 
	 * @param coveredBranches
	 * @param coveredMethods
	 * @param fitness
	 */
	private void printStatusMessages(
	        AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite,
	        int coveredBranches, double fitness) {
		if (coveredBranches > maxCoveredBranches) {
			maxCoveredBranches = coveredBranches;
			logger.info("(Branches) Best individual covers " + coveredBranches + "/"
			        + (totalBranches * 2) + " branches");
			logger.info("Fitness: " + fitness + ", size: " + suite.size() + ", length: "
			        + suite.totalLengthOfTestCases());
		}
		if (fitness < bestFitness) {
			logger.info("(Fitness) Best individual covers " + coveredBranches + "/"
			        + (totalBranches * 2) + " branches");
			bestFitness = fitness;
			logger.info("Fitness: " + fitness + ", size: " + suite.size() + ", length: "
			        + suite.totalLengthOfTestCases());
		}
	}

}

