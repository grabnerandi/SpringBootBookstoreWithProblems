# SpringBootBookstoreWithProblems
This is an extended version of the Spring Boot Sample App I found here: https://spring.io/guides/gs/circuit-breaker/

## Overview of new version of this app
![Architectural Overview and REST Endpoints](images/ShiftLeft_HOTDAY2017_1.png)

## Dynatrace PERFORM 2017 HOT DAY
We are going to use this app for our [Dynatrace Perform 2017](http://perform.dynatrace.com) Hands on Training Day in our session "Shift-Left Performance: Integrating Dynatrace in your Pipeline".
In that training session  we are going to build a Jenkins Build Pipeline to build and deploy this 2 tier Spring Boot App. 
We will also execute a handful of HTTP Web API Tests against the REST APIs. The [Dynatrace AppMon Jenkins Integration](https://wiki.jenkins-ci.org/display/JENKINS/Dynatrace+Plugin) and [Dynatrace Test Automation](https://community.dynatrace.com/community/display/DOCDT65/Test+Automation+Explained) feature allow us to stop the pipeline in case a code change to the app is going to impact performance, scalability or if it introduces any architectural rules

Here is a quick overview of what we are trying to achieve in that training session:
**Building a Jenkins Pipeline for this App**
![Multi-phase Jenkins pipeline](images/ShiftLeft_HOTDAY2017_2.png)

**Switching from Version 1 to Version 2 of this App**
![Two versions of this app available. Version 2 has some built-in architectural issues such as making too many micro-service calls to the backend!](images/ShiftLeft_HOTDAY2017_3.png)

**Dynatrace automatically detects the regression**
![Dynatrace detects a regression on the number of micro-service calls being made while executing our tests](images/ShiftLeft_HOTDAY2017_4.png)

**Dynatrace Jenkins Plugin stops the pipeline**
![Dynatrace Jenkins Plugin stops the Jenkins Pipeline](images/ShiftLeft_HOTDAY2017_5.png)
