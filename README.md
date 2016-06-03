#rtce
Real Time Collaborative Protocol

## Use

###Server

Run the jar file RTCEServer.jar (the command is java -jar RTCEServer.jar).  At this point, the only maintenance to be done on the server side is restarting, which should only be done in the event of something breaking.

###Client 

Run the jar file RTCEClient.jar (the command is java -jar RTCEClient.jar).

On starting the client, it will attempt to automatically discover a server.  If it fails, it will ask for the IP or hostname of the server.

For all commands listed below, replace values in {} (such as {username}) with appropriate values.

Do not abort the client except through logoff.  In the current state, the server is not prepared to recover from poor termination, and 

####login

To begin a client session, type the command login,{username},{password},{documentOwner},{documentTitle} and press enter.
For the current implementation, {documentOwner} should always match {username}, and {documentTitle} does not matter, since the current application only has a single example document hard-coded.  However, there are existing files example1 and example2 (another title will create a new file).
The valid combinations of username and password are:
cs544,cs544
group4,password
Thus, valid logins which will not create a new file include:
login,cs544,cs544,cs544,example1
login,group4,password,group4,example1
login,cs544,cs544,cs544,example2
login,group4,password,group4,example2

####request

To request to edit or create a section of the document, type request,{sectionNumber}.  Thus, request,1 will request to edit section 1, while request,5 will request to edit section 5, and so on.  Section 0 is a placeholder for the beginning of the document and should not be edited.  However, you can create a new section by choosing an unused number, and can place it anywhere in the document.

Once a request has been made, the token is reserved for 45 seconds.

####commit

To push changes to the document, type commit,{prevSection},{curSection}.  This can only be done after a request while the token is still reserved, and {curSection} must match {sectionNumber}.  {prevSection} is the number of the section before the edited section, and is used if a new section is being created.  So, commit,1,2 will allow you to edit section 2 if you previously issued request,2.  If you previously issued request,7 and no section seven exists, commit,6,7 will let you add section 7 to the end of the document, commit,0,7 will allow you to add section 7 to the beginning of the document, or commit,3,7 will allow you to add section 7 immediately following section 3 (so, between section 3 and 4, assuming no other section has been added between them).
Following the issue of the commit request, you will be prompted to enter the new text.  This text will replace the currently existing text.  If you want to make a small edit (eg. in section 2 change "some one" to "someone") it is recommended to copy the old line, paste it in the prompt, and then make the edit to the pasted line before pressing enter.

####block

If you are the owner of a document, you can block another user for a period of time.  To do so, type block,{username}.  In the current application, this is fairly limited.  The owner of the document is the first user to log in.  You can only block someone who has already logged in.  The block lasts for time indicated by the servConfig.conf file (default 5 minutes).  So, if you first log in as cs544 and then someone else logs in as group4, you can issue block,group4 and then that person will be unable to edit the document for 2 minutes.

The block time is 2 minutes.

####logoff

Ends the session, gracefully.

## Credits
This is the final project for Group 4 in CS544.