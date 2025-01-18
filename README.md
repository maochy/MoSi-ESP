# MoSi-ESP：Multi-objective Swarm intelligent for Edge Server Placement

## Introduction

This project is a **Java** implementation of MoSi-ESP for the Edge Server Placement (ESP) problem, which considers minimizing the total energy consumption and balancing the load among servers.

## Dataset

The dataset used for the experiment is [Shanghai Telecom Dataset for Mobile Edge Computing](http://sguangwang.com/), open-sourced by Prof. Shangguang Wang's team at Beijing University of Posts and Telecommunications. The description of data preprocessing is displayed in file directory named by [Data Preprocessing](./Data%20Preprocessing/README.md).

## Experimental Hardware Configurations

All the experiments were conducted on a PC equipped with an Intel Core i7-7700 CPU processor, 32GB RAM, and the Windows 10 operating system.

## Project Directory Structure

```markdown
./
├─MoSi-ESP
|    ├─.classpath
|    ├─.project
|    ├─.settings
|    ├─bin
|    ├─src
|    |  ├─MoSi_ESP_main.java
|    |  ├─utils
|    |  |   ├─AlgorithmUtils.java
|    |  |   ├─ExperimentalUtils.java
|    |  |   └─TestItem.java
|    |  ├─model
|    |  |   ├─BaseStation.java
|    |  |   ├─ConstNum.java
|    |  |   └─ESP.java
|    |  ├─method
|    |  |   ├─MoSi_ESP.java
|    |  |   └─SolutionItem.java
|    |  ├─jmetal_extended
|    |  |        ├─ArrayArrList.java
|    |  |        ├─ArrayArrListSolutionType.java
|    |  |        └─XArrayList.java
|    ├─data
|    |  └─BaseStationSet.csv
|    └─jmetal4.5.2.jar
|
├─Data Preprocessing
|         ├─data_preprocessing_main.java
|         ├─data_6.1~6.15.csv
|         ├─BaseStationInfo.csv
|         ├─BaseStationDataset.csv
|         └─Description of the data preprocessing process.xlsx
|
├─Experiment Results 
|         ├─Comparison on the Different Communication Distances.xlsx
|         ├─Comparison on the Different Numbers of Base Stations.xlsx
|         ├─Impact Analysis on Swarm Size.xlsx
|         └─Impact Analysis on Iteration Number.xlsx
```
