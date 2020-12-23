# MarkMath
Repository of MarkMath for Hypatia

How to run the app?
Run the Login class to run the app. The password is set to "2020fall" since you will be downloading our database, with already some sample classes, students and assignments. We thought this would be easier to test. If you had started with opening the app for the first time and an empty database, you would be asked to create an account with a new password.

What libraries are needed?
All the libraries that are needed can be found in the libraries folder inside the idea folder.

Want more information on each role of the class?
All files support javadoc. So, run javadoc to have all the html pages needed to describe in detail the role of each class or method.

How to test the program?
The Help feature in the program tells everything needed to work with the app. If you want to test the marking functionality, you can create an Assignment Template in Hypatia, create some checkmath expressions, give the file the name of the form "StudentID-NameOfAssignment" (ex: 22-Derivatives), and you will be able to mark it. These instructions are also in the program, just summarized here.

Why is the app freezing when marking an assignment if I am using the latest version of Hypatia?
You may notice that if you try marking the assignment and you have the latest version of Hypatia, the app will freeze for 7 seconds. You can notice the markAssignment code has a threadfreeze of 7000 milliseconds. That is not our fault, however! We had to freeze the app because of a change that was made on how Hypatia sends data through our local server. In fact, before we received data immediately, and the marking button worked in a really fast time, but now Hypatia takes approximately seven seconds to send all the information we need, and so we had to freeze our app. The TLI instructors saw in the presentation made with the old version of Hypatia that the mark functionality works immediately with the old version of Hypatia, and they said they would fix this problem.
