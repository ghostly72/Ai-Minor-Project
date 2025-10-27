# Artificial Intelligence Assignment – CSMI17

This repository contains the solutions and reports for the **CSMI17 Artificial Intelligence** course assignment.  
It covers two classic AI problems implemented and analyzed using different techniques.

---

## Problem 1: Robot Path-Finding using A* Search

### **Objective**
A robot must find a path from a start cell to a goal cell in a grid that contains random obstacles.

  **Approach**
  - Implemented **A\*** search algorithm.
  - Used three different heuristics:
    - **Manhattan Distance**
    - **Euclidean Distance**
    - **Diagonal Distance (Chebyshev)**
  - Randomly generated grids with random start and goal cells for testing.


### **Outcome**
Performance of heuristics was compared using graphs to identify which heuristic achieved faster convergence and optimal pathfinding efficiency.


## Problem 2: Timetable Generation as a Constraint Satisfaction Problem (CSP)

### **Objective**
To automatically generate a valid timetable by satisfying constraints such as no time-slot or room conflicts.

### **Approach**
Two CSP solving techniques were implemented:
1. **Backtracking with MRV & LCV Heuristics**
2. **Backtracking with Forward Checking**


### **Outcome**
Forward checking demonstrated better pruning and reduced backtracking steps, while heuristic-based backtracking performed competitively on smaller datasets.

---

## Setup
- Language: *Java* (for A\* and CSP implementations)
  
