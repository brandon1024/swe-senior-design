## Preface
A good understanding of Git is an incredibly valuable tool for anyone working amongst a group on a single project. At first, learning how to use Git will appear quite complicated and difficult to grasp, but it is actually quite simple and easy to understand.

Git is a version control system that allows multiple developers to contribute to a project simultaneously on a project. It is a command-line application with a set of commands to manipulate commits and branches (explained below). This tutorial will help you get started, and in no time you will be a Git Ninja!

## How Git Works
### Branching and Commits
One of Git's biggest advantages is its branching capabilities. This allows someone to branch off of the master branch (where the production-quality code remains) and work on a feature or fix independently of the rest of the project. Once they finish their work, they can merge the branch back into the master branch with the click of a button.
```
FEATURE              o-----o-o---------o-----o
                    /                          \
MASTER    <---o----o------o--o-------------------o------>
```
Feature branches provide an isolated environment for the developer to work. They are also useful for organising different levels of granularity. For instance, in what is known as the Git-Flow, there are three important branches: master, staging, and develop. Master is the parent branch, representing the state of the project that is released to the public (deployed). The staging branch is where pre-release QA is done. Develop is where developers work on the project. All feature branches are branched from develop.
```
FEATURE1              o-----o-o---------o-----o
                    /                          \
DEVELOP   <-------o-----o---o-----------o-------o------>
                         \               \
STAGING   <---------------o---------------o----o-------->
                           \                    \
MASTER    <------------------o-------------------o------>
```
In the diagram above, commits are represented by `o` in the branches. A commit is an individual piece of work. It's a bit like a milestone--when a small piece of the task is completed.

### Workspace, Staging, Local, and Remote
<img src="http://blog.podrezo.com/wp-content/uploads/2014/09/git-operations.png" align="right" width="512" />
A Git repository is also split into four levels. Each level represents a state of the codebase. Each state is isolated from each other, but git commands allow you to transfer data between them.

- The workspace is the folder on your computer. This is where all the work happens.
- The staging area is where changed files are placed before making a commit. It allows you to essentially select only the files you want committed.
- The local is your copy of the repository. No one else will see this except you.
- The remote is the public repository. Everyone sees the same remote.

So, evidently, moving changes from one workspace to another will involve passing from workspace1 > staging1 > local1 > remote > local2 > staging2 > workspace2. It is also important to note that all communication must pass through the remote repository.

## Installing Git on your Machine
If you're using UNIX, you're in luck! Git come pre-installed on macOS most distributions of Linux. Type `git --version` to check if you have Git installed.

Git does not come pre-installed on Windows (big surprise...), so you will need to install it. [Git Bash](https://git-for-windows.github.io/) is a great option; it emulates a UNIX shell environment so you don't need to use Windows commands. You can also download a version of Git [here](https://git-scm.com/download/win) that you can use in the Command Prompt. There are also graphical Git clients, a quick google search will show a number of them.

## Git Setup (Login)
Let's get you setup to use Git. First, you need to identify yourself. When you first install Git, it's important to set your user name and email address because every commit uses this information. In the terminal:
```
$ git config --global user.name "John Doe"
$ git config --global user.email johndoe@example.com
```

## Cloning the Repository
If you have already set up your development environment through IntelliJ using the `Checkout From Version Control` option, then you can skip this section. Otherwise, you will need to clone the repository to your local machine. Fortunately, this is a very easy process.

Open the terminal (or Git Bash if using Windows) and navigate to the folder `~/IdeaProjects/`. Now, run the following command: `git clone https://github.com/SWE-4103-Group-3/Project.git`.

This should create a new child directory `Project`. Now navigate to the project folder. From here, you are able to run your git commands.

If the clone didn't work, try using the `Checkout From Version Control` option in IntelliJ.

## Useful Git Commands
#### git status
> Shows the status of your current branch. It will tell you which files you have modified, which files are stages for commit and which files are not. This command is particularly useful in combination with `git add` when you only want to commit certain files.

#### git branch
> Shows a list of your local branches. You can also add these options to do other branch-related things:
> - `-d or --delete`: Delete a branch. e.g. `git branch -d feature-branch-name`
> - `-D`: Force delete a branch. This is used when you want to delete a branch that has changes that weren't pushed to the server. e.g. `git branch -D feature-branch-name`
> - `-a or --all`: Display all local and remote-tracking branches.

#### git fetch
> Fetches branches from the server, along with their history and information. You can also use `git fetch origin` to update your remote-tracking branches.

#### git checkout
> This command has a few functions. It can be used to create a new branch off of the current branch, it can be used to checkout a branch from the server, or switch to another branch.
> - `git checkout develop`: you will use this a lot. It will switch you from whatever branch you are currently in to the `develop` branch. If you have changes in your current branch, you will need to either stash them or undo your changes before you switch branches.
> - `git checkout branch-on-server`: If you want to work on another persons branch (assuming they already pushed their branch to the server), you can use this command to create a copy of the branch on your local machine. You will need to do `git fetch` first to grab the information about the branch.
> - `git checkout -b new-branch-name`: create a new branch off of the current branch. If you are in `develop`, this will create a new branch from develop.

#### git add
> You made changes to your branch, and you want to stage your changes for committing. You will need to add them. There are two ways to do this:
> - `git add /filepath/file.f`: this will add the single file.
> - `git add .`: this will add all the changed files. Be careful here; make sure you aren't adding unwanted changes by reviewing your changes using `git status`.

#### git commit
> You want to make a commit! Awesome! Here's how you do it:
> - `git commit`: This will make a commit of the files you have staged using `git add`, and will open a VIM editor to enter your commit message.
> - `git commit -a`: This will make a commit of add changed files, and will open a VIM editor to enter your commit message. This is the same as doing `git add .` and `git commit`.
> - `git commit -m "commit message"`: Skip the VIM editor and put your entire message in the command line!
> - `git commit -a -m "commit message"`: Combine all of it together! Fab.

#### git push
> Once you make a commit, you will need to push your changes to the server to make it visible to the world. Once you push, your branch and commit(s) will be visible to others.
> - `git push branch-name` or `git push origin HEAD`: Push your branch upstream. You can use either version; using origin HEAD simply means you are pushing the tip (HEAD) your branch to the same name on the server.
> - `git push origin -f branch-name` or `git push origin +branch-name`: <sub>:heavy_exclamation_mark: :heavy_exclamation_mark: :heavy_exclamation_mark: **BE CAREFUL: This is a scary command, and you should only use it if you really know what you are doing. It essentially overwrites the branch you specified on the server with your local copy of the branch.** :heavy_exclamation_mark: :heavy_exclamation_mark: :heavy_exclamation_mark:</sub>

#### git rebase
> <img src="https://i.stack.imgur.com/5yRg3.png" align="right" width="256" />
> Say you are working on a feature branch `SWE-feature` off of `develop`. You are modifying a method called `bobsYourUncle()`. You change a line in the method and make a commit (without pushing), but another person working on another branch changes that same line first and his changes are eventually merged into develop. When you try to push, you will be stopped because there are merge conflicts. To resolve these conflicts, you will need to perform a rebase.
> If you are in this situation, I suggest reading tutorials on how to resolve merge conflicts, or get help from someone with experience.

#### git stash
> Stashing allows you to save your current unstaged changes and bring your branch back to an unmodified state. When you stash, your changes are pushed onto a stack. This is especially useful if you need to quickly switch to another branch without having to commit your incomplete changes.
> - `git stash`: stash your unstaged changes.
> - `git stash pop`: unstash your changes.
> - `git stash drop`: drop your stashed changes. Careful, you will lose your changes!

#### git reset
> Git reset is used to unstage files or remove commits.
> - Undo `git add`:
>     - `git add .`
>     - `git reset -- file.html` (unstage single file, or)
>     - `git reset`              (to unstage all)
> - Undo most recent commit:
>     - `git commit -a -m "commit message"`
>     - `git reset --soft HEAD^` (undo commit that is at the tip of the branch)
>     - `git commit -a -m "new commit message"`
> - Undo a commit permanently:
>     - `git reset --hard HEAD~2` (destroy the last two commits, i.e. HEAD and HEAD^)

#### git log
> Show the history of all branches and their commits. Useful for seeing the most recent commit and getting commit hashes.

## Git Faux Pas
Git is awesome, but it can be dangerous. We have all heard horror stories of new co-op students in the work force destroying a codebase after accidentally running a command they weren't sure about (have you seen [this reddit page](https://www.reddit.com/r/git/comments/6tl7zu/your_git_horror_stories/)?). Make sure you understand what you are doing before you do it. Double check. Triple check. Quattuordecuple check. The standard commands (`git add, git commit, git push, git pull, git checkout, git branch`) are all mostly harmless, so don't let git scare you. Having an understanding of what is going on under the hood will be a great way to make sure mistakes don't happen.