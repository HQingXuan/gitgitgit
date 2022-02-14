# Gitlet Design Document

**Name**: Haoqing Xuan, Chenkai Mao, Yuxi Liu

## Classes and Data Structures
## Main.class
Main class takes all inputs and compares them to the existing commands I have. Main class also takes care of all error messages that are required in the spec.

**Fields**
1. compare the input with the current existing command names and use different cases to run different commands.
2. If the input has corresponding error message(error that happen in the input), main class should recognize the error and show error message.
3. should take the input as argument and use the first part of the argument as the command. 
4. should call all following commands depending on the first part of the input [add, commit, log, global_log, checkout, find, status, branch, rm_branch, reset, merge].
5. When the first part of the input is checkout, be sure to check how many elements are in the arguments and then execute the corresponding checkout method.

## stage.class
Stage class should be the area in git directory that store the contents of files to the blobs variable and point the blobs variable to corresponding files.

**Fields**
1. Should create two maps that store the information of files that are about to be staged for adding or removing.
2. Should initialize the two maps every time a stage() class object is created. 
3. should have helper methods that can return the maps in stage class and can be used in other class to access variables in stage class. 


## Commit.class
commit class takes a snapshot or saves the entire directory at the current moment. And update files that are in the staging area.

**Fields**
1. Should create variable that stores the following information: [commitMessage, the SHA1 code of the commit, the time of the commit, 
a blobs Map that will store the fileName and corresponding SHA1 contents in the file, a string to indicate the current branch of the commit,
a string that stores the SHA1 information of the previous commit, and a stage class variable to record the current stage of each commit. ]
2. If there's no previous commit, all setup should be new and the commit should point to no previous commit. 
3. If there's previous commit, the current commit's previous commit variable should be the previous commit's SHA1 code and branch should be 
updated. 
4. Should copy all files and contents from previous commit into the current commit. 
5. give timestamp and SHA1 code to the commit.
6. point the current commit to last commit. (intuitively linked list can do the job) (Satisfy the runtime requirement)
8. Log should have the functionality of reflecting the entire committing history. (should print out all commits with corresponding information)
9. implement the toString() method which helps format the information what will be presented by the log() method. 


## Branch.class
branch class tracks different path of the commits. branch class also should take care of the merge functionality.

**Fields**
1. Set the master pointer to the current branch.
2. Create branch name (a new pointer) to other branches.
3. one pointer is active, HEAD pointer.
4. Able to switch the active pointer by checkout [branch name]
5. Merge functionality, merge files from a given branch to the current file. (overwrite the current branch)

## Blobs.class
blobs class provides the basic setup of blob type files. When we read the file, we want to store the contents of the file 
as byte array, and also provide another variable to store the SHA1 information of the content so that we can keep track of what is 
inside the file using SHA1.

## Repo.class
Repo class will take all implementations of methods that will be used in gitlet design. The reason of putting all implementations in one 
class instead of distributing them into different classes is that it is better for us to keep track of what we have done so far in implementing. 
Clearly, it is not as organized as distributing methods to classes that they actually belong to, but this design currently fits our ability the best.

create directory: 
1. locate the current working directory by using System.getProperty("user.dir).
2. create the pathname .gitlet inside the current working directory. 
3. create the following directories inside .gitlet directory [commits, blobs, branch, stage].
4. create directories for adding and removing inside the stage directory. 

Main methods:
1. add-[fileName] should execute the add method that described in the gitlet spec. 
2. commit-[message] should execute the commit method that described in the gitlet spec. 
3. log() should take no parameter input and should use the toString() printing method to present the commits information that are written in commit directory. 
4. global_log() should take no paremater input and should execute the global_log method that is described in the gitlet spec. 
5. checkout-[fileName] should execute the checkout method that is described in the gitlet spec by calling the checkout-[commitID]-[fileName] method.
6. checkout-[commitID]-[fileName] should take commitID in the commit history and the fileName that we want to restore and execute the checkout 
method that is described in the gitlet spec. 
7. checkout-[branch]
8. find-[commitMessage]
9. status() should take no parameter input and present the information of the staging area [adding] + [removing], branches, and files that have been modified but not added to the stage. 
10. branch-[branchName]
11. rm_branch-[branchName]
12. reset-[commitID]
13. merge-[branchName]

helper method: 
1. need one helper method to get the current commit from the commit directory. 
2. need one helper method to save the commit into the commit directory. 


## Algorithms
1. Store contents of files into Blob variable, thinking about hashmap, so that can match contents with specific file name.
2. give each commit a timestamp and hashcode and use (intuitively) linked list to connect each commit.
3. Should be able to traverse the linked list and find the commit at given position and restore that commit. So there might be a pointer to the specific contents of the commit that can be restored.
4. Branch might also be connected by a tree. Since tree can have separate and different branch. Need to traverse the tree to find the given branch and then access the contents in that commit and then overwrite the current branch.
5. After merging, the state should be saved to commits.

## Persistence
1. After each change, the change should be saved to commits.
2. After merging, the change should be saved to commits. 
3. create all working directories when the init() method is called. 
4. We will store every commit in the .gitlet/commits directory with pathname .gitlet/commits/[CurrentSHA], which each [CurrentSHA] 
represents the SHA1 code of the commit information, and will be written to the directory using writeObject method in Utils. 
5. the current branch of the commit tree is represented by a string naming the branch of the current commit ["master"],
the branch name is updated each time commit method is called. The result will be pointing to a [branch].txt file in the branch directory. 
6. We will store all files that are added to stage in the .gitlet/stage directory with pathname .gitlet/stage/add/[fileName] if the file is being added
and .gitlet/stage/rm/[fileName] if the file is staged to be removed. 
7. 