
# oapenmemomanager

MEMO Manager provides the environment to create and manage 

* clients and their tasks
* library scripts and queries
* users 
* settings

MEMO Manager stores all data - clients, tasks, settings, users, scripts and queries - in the MEMO database.


## Configuration

* `server.port`
   Choose a free port (e.g. 8084) to map your external web server (NGINX) to.
* `spring.datasource.url`
   Database url (`jdbc:mysql://12.34.56.123:3306/oapen_memo?reconnect=true&rewriteBatchedStatements=true`)
* `spring.datasource.username`
* `spring.datasource.password`
   Login credentials 
* `path.exports`
   Root directory for the generated export files (`${user.home}/oapenmemo/exports/`). OAPEN Task Runner writes its output here.
   Exported files must be downloadable from MEMO Manager as well.
* `taskrunner.url`
   MEMO Manager needs to know where	the task runner is, because users may invoke tasks directly here (either run or dry-run).
   (`http://localhost:8085`)   


## Installation and running

This application must be installed as a service.

- Copy `manager-x.y.z.jar` to the user's (`oapen`) home directory (or a sub-directory thereof);
- Create a symlink `ln -s manager-x.y.z.jar manager.jar`;
- In `/etc/systemd/system` create a file named `oapen-memo-manager-website.service`. See
  [Readme-memomanagersite-service.txt](./Readme-memomanagersite-service.txt) for an example.  
- Create a mapping on your web server to access the application from the internet (NGINX example):
        
        server {
            
            server_name memomanager.oapen.org;
            
            location / {
                proxy_set_header Host $host;
                proxy_set_header x-forwarded-for $remote_addr;
                proxy_pass http://localhost:8084;
            }
        }
    
- Run `certbot` or use an equivalent tool to obtain an SSL Certificate and enforce secure connections.


## Usage

After login the Clients screen appears. Clients can be created and managed here.


### Managing Clients

Clients are defined by
 
* **client name**   
  Free text field 	
* **user name**   
  This value is used for sining in as well as the name for the exports directory for this client. Certain restrictions are in place  
  on the characters to be used (user name can only contain A-Z, a-z, 0-9 and _).
* **access key**   
  A 32-character hex key to be included on urls with restricted access
* **password**   
  Passwords can only be generated here. They must be copied immediately after creation and communicated to the user. Once saved, passwords
  are encrypted and can not be deciphered. Users have no means of setting passwords themselves.
* **notes**   
  Any text that must be stored along with the client.

A client can only be removed when there are no tasks attached.

Note: User name `tmp` cannot be created (it is a reserved name for the dry run directory). Assigning `tmp` as user name results in a warning and
the record cannot be saved (`TMP` however is allowed since the system is built for UNIX file systems).


### Managing Tasks

Tasks consist of a main Python script and a(n optional) query, together with fields defining when and with what frequency the task will be run.

The given task extension must match the file output, so to inform users correctly of the type of file being served.

> Queries must be written as pure SQL, not as Python code. When imported they will be automatically rewritten to a Python string variable named `query`. This variable `query` can then be used in Python code (whether or not prefixed with an alias).


#### Writing main Python script

The main Python script must always provide `print` statements printing out the desired output to the (Docker container) console. 
When the Taskrunner runs a task, output sent to the console by the script will be picked up by the Java container and processed further.

So a typical script looks like this:

    [imports]
    
    def myfunc():
    
       [function body]
       
    if __name__ == '__main__':
       print(myfunc())   
       
       
NOTE: When generating large exports, to prevent the Python process from consuming to much memory, it may be preferable to regularly flush data to the console, rather than collecting all output in a variable and print all data at the end of the report generation. See the example below:

    [imports]
    
    def myfunc():
    
       ...

       for row in rows:
          print(row)

       ...
       
    if __name__ == '__main__':
       myfunc()   


> All output printed to the console will be collected and included in the export when the Python process finishes.
 


#### Python library imports

MEMO Manager Python scripts can import Python code from two sources:

1. libraries that are installed on the host System, which is a Docker image. 
Packages not already available here (like `numpy`) require a new Docker image to be built and installed (add the appropriate `pip3 install` line to the Docker file en run `docker build`). See [Taskrunner documentation](https://github.com/trilobiet/oapenmemotaskrunner/blob/main/README.md) (section 'A Docker image to run Python scripts') for an overview of imported packages and instructions on how to build a new image. 
2. libraries that are part of the MEMO Manager Library (scripts and queries). Library scripts themselves can import available libraries as referred to under 1.


#### Including main query in main Python script

The main query for each task can be included in the main Python script using a regular Python style import, using this scheme:

    from queries.[lowercase client username] import [lowercase task name without extension]_[lowercase task extension]
    
Example:

    # Client name : ABC Corporation
    # User name   : ABC_Corp
    # Task name   : Some_Report.xml
    
    from queries.abc_corp import some_report_xml
    ...
    cursor.execute(some_report_xml_rss.query)
    
Or, when using an alias    

    from queries.abc_corp import some_report_xml as my_alias
    ...
    cursor.execute(my_alias.query)


#### Including library query in main Python script
 
Library queries can be included using this scheme:

    from queries import [library query name]

Example:

    # Library query name: full_text_search
    
    from queries import full_text_search
    
> Clicking the `BROWSE LIBRARIES` button opens a library popup screen that allows you to select a library and copy 
its import statements to be pasted in the main script.  


#### Parameterized queries

Since (library) queries are wrapped in a Python String variable, they can be parameterized. Parameterized queries can be abstracted to not contain specific filter terms and thus be more widely applicable.

Example: A library query looking for titles from a single publisher (defined by `title.handle_publisher`)
and a given year (`title.year_available`):

	# Library query titles_publisher_year
      
	SELECT 
		*
	FROM 
		title
	WHERE 
		handle_publisher = '{pub_handle}'
		AND year_available = {pub_year}
	
This query can then be called from a script as follows (publisher **20.500.12345/12345** and year **2025**):

    from queries import titles_publisher_year
    from sniplets import mysql_connect
    
    pub_query = titles_publisher_year.query.format(
       pub_handle = "20.500.12345/12345",
       pub_year = 2025
    )
    
    # connect and query
    connection = mysql_connect.connection
    cursor = connection.cursor(dictionary=True)
    cursor.execute(pub_query)
    records = cursor.fetchall()



#### Including library script in main Python script

Library scripts (sniplets) can be included using this scheme:

	from sniplets import [library script name]

Example:

    # Library script name : mysql_connect

    from sniplets import mysql_connect
    ...
    connection = mysql_connect.connection
    
> Clicking the `BROWSE LIBRARIES` button opens a library popup screen that allows you to select a library and copy 
its import statements to be pasted in the main script.  


#### Multiple includes and aliases

Multiple query or sniplet includes can be combined in a single import statement (following standard Python syntax):

    from queries import some_query as a_query, another_query, yet_anotherquery
    ...
    cursor.execute(a_query.query)
    ...
    cursor.execute(another_query.query)
    ...
    cursor.execute(yet_anotherquery.query)



    from sniplets import mysql_connect as connect, mysql_connect_remote as remote_connect
    ...
    this_connection = connect.connection
    ...
    that_connection = remote_connect.connection


#### Developing and testing tasks

Developing and testing of tasks can best be kept out of a live system. When testing locally make sure no packages are imported that are not part of the live Python installation. See [Taskrunner documentation](https://github.com/trilobiet/oapenmemotaskrunner/blob/main/README.md) (section 'A Docker image to run Python scripts') for an overview of imported packages.

Anywhere on your development machine create a file structure like this (mimicking the taskrunner generated structure):

    [root dev directory]
       |
       |-- queries
       |      |-- ...
       |
       |-- sniplets
       |      |-- ...
       |
       |-- mainscript.py
       
> Note: import paths for queries and sniplets are relative to the root dev directory.       

Queries or library queries to be imported must be converted to a (multi-line) string named `query`.

So, a query like 

    SELECT
    	*
    FROM	
    	some_table
    	
goes in a file like this

	query = '''
    SELECT
    	*
    FROM	
    	some_table
    '''	
    	
> Note: Don't do this on production. On production any input in a query labeled field is automatically converted to a string named `query` by the Taskrunner.    	


#### Running tasks from the manager

Tasks are scheduled and run by the Taskrunner application. Tasks can be run immediately from MEMO Manager as well,
for testing purposes, or whenever a task must be re-run manually.

*Dry running*:

From the task editing screen a button 'DRY RUN' is available to run the task and get its output. Dry running a task 
does not interfere with its schedule nor will it overwrite any previously generated scheduled output (though it will override a previous dry run). 
When the task finishes successfully the result can be downloaded immediately. Failed tasks provide an error message. 

> TIP: queries can best be tested in an external application (e.g. MySQL Workbench) before pasting them in the MEMO query editor.
> An external application may provide better feedback on query performance, duration and SQL syntax errors.

Trying to dry-run a task during a scheduled period is prevented and will result in an error message (settings `taskRunner.busy.starttime`
and `taskRunner.busy.hours` in MEMO Taskmanager).

*Running*:

Tasks can also be run live on production from the client's task overview (click `run now` in the tasks table). 
**Note:** Tasks being run from here will overwrite output from any previously run task, either run manually 
or scheduled by the Taskrunner. The overwritten output is also immediately visible to the customer in the MEMO Client web. 
Running a task manually through the `run now` button does not interfere with its schedule.


#### Task running schedule

A Task runs first on its start date and then according to schedule daily, weekly, monthly or yearly. Task frequencies are defined as DAY, WEEK, MONTH or YEAR. Only activated tasks run. When a task is activated *after* its start date it will run for the first time on the next scheduled date relative to its start date.

When a Task's frequency is defined as MONTH or YEAR then that Task will only run when the day of month defined in the Task's start date exists in the current month.

> A Task with `startDate = 2024-02-29` and frequency `YEAR` will start in February 2024 and then run only every 4 years, so in February 2028, 2032 etc.

> A Task with `startDate = 2024-01-31` and frequency `MONTH` will run in January, March, May, July, August, October and December, but not in February, April, June, September and November.
    
So to be sure, monthly and yearly tasks must have their start date set on any day from the interval 1 until 28.

**Note:** When a task is skipped for some reason (network or service down) the task will not run until the next scheduled date. So the scheduler will not make up for skipped tasks. You need to run the task manually or wait for the next scheduled run.


#### Task export visibility

Tasks are private by default. This means that in order to download the task's output, the key (client field `access key`) must be appended to the export URL. Tasks that are set public do not require an access key and a public link appears in MEMO Client Web.


### Managing Library Queries and Scripts

Library scripts and queries can be used to share functionality between tasks. For instance, database connection data that is shared by queries in multiple tasks can be wrapped in a centralized library script that can easily be updated should the connection parameters change.

> Library queries must be written as pure SQL, not as Python code. When imported they will be automatically rewritten to a Python string. 

See above ('Including library ...') for instructions on how to include library scripts and queries into the main Python script.

Library queries and scripts feature a list of tasks that include them. Only when this list is empty can the library item be deleted.


### Managing Settings and users

Settings are key-value pairs for use elsewhere. They have no use in the Task Manager. 

Predefined settings:

| setting                 | usage                                                                                  |
|-------------------------|----------------------------------------------------------------------------------------|
| `clients.contact.email` | Used by MEMO Client Web: displays application manager contact address for MEMO clients |


Users with the `administrator` privilege can create and manage other users. Passwords can only be generated here. They must be copied immediately after creation and communicated to the user. Once saved, passwords are encrypted and can not be deciphered. Users have no means of setting passwords themselves. 

A System Super Administrator is always available and cannot be deleted. Its password must be set during database install using BCrypt hashing (https://bcrypt-generator.com/).


## Annex: MEMO Database installation

MEMO runs on MySQL v 8.0 or higher. All 3 applications (MEMO Manager, MEMO Client web and MEMO Task Manager) need access to this database.

The database create script can be found [here](./dev/db/oapen_memo_create_MYSQL.sql).

For the System Super Administrator to be able to login to MEMO Manager and create other users a default account has been created.

    user: administrator
     pwd: secret
     
**It is strongly advised to create a new unique Super Administrator password. This password must be BCrypt hashed [https://bcrypt-generator.com/](https://bcrypt-generator.com/) and its hashed form stored in the database before taking the system to production.**

