# Georgian Bay Tutle Hospital Volunteer Tracking App


## Iteration 2 - Review & Retrospect

 * When: 2020/03/10
 * Where: Online

## Process - Reflection


#### Q1. Decisions that turned out well

List **process-related** (i.e. team organization and how you work) decisions that, in retrospect, turned out to be successful.
 
1. We made rules regarding our Github repository: no one should work on master branch or push directly to master branch. To push to master from other branches, one should create a pull request and at least two other team members review the request before merging it. Any pull request with merge conflict is not accepted. In this way, we could keep master branch clean and away from merge conflicts. 

2. Before we start any coding, we first made REST APIs and maintained in restapi.md file. In this manner, we could separately work on front-end and back-end. In front-end development, we used fake data instead of http requests. In back-end development, we tested APIs using curl command or Postman program. After both ends are finished, we just replaced fake data to http requests and it worked blissfully.

3. Slack is a good communication tool for large team project. We made several channels such as backend, frontend, and general. Different discussions could be made simultaneously in relevant channels. It was also efficient to grasp relevant up-to-date decions as we had to work back and forth from other courses' assignments. For example, a member who is only working on frontend can just focus on frontend channel.

#### Q2. Decisions that did not turn out as well as we hoped

List **process-related** (i.e. team organization and how you work) decisions that, in retrospect, were not as successful as you thought they would be.
 
1. We first decided to use JHipster to generate templates for back-end and web application. We wanted to save time by generating templates since we also have to make Android app. However, it was not easy to use JHipster and Spring server. None of us had used JHipster and Spring before. We expected to learn this quickly since we know Java. We spent about 4 days on this but didn't make much progress. We eventually switched to Node.js and mongoDB since one of our members had an experience with it.

2. The code review process was not as thorough as we hoped. It was often difficult to get 2 people to review pull requests. As a result, pull requests would not be merged often. Then when someone created a branch from master to work on a feature, they were working on an outdated version which caused merge conflicts later. We often merged pull requests with only 1 code review. However, the decision is still good practice so we will keep it.

3. The team lead thought we would all monitor Trello. However, most members have only checked Slack. Our partner didn't CC for some emails, and team lead didn't notice this and only updated Trello. Most people noticed it later and had to alter implementation accordingly. After this happening, we all notified everything on Slack.


#### Q3. Planned changes

List any **process-related** (i.e. team organization and how you work) changes you are planning to make (if there are any)
 
 1. We will not rely on everyone checking Trello to know their tasks. Trello will still be maintained for organization but, we will notify people on Slack channels or DMs about tasks. This will allow all members to stay up-to-date which will make development more efficient.
 
 2. Notify people on Slack about pull requests and ask specific group members to review the code. This way people are aware that there are changes to code that need to be merged. Specifically asking someone to review the code increases the chances that the pull request gets 2 reviews.


## Product - Review

#### Q4. How was your product demo?
 
 We demoed via video call because our partner lives far from Toronto. One of our members gave the demo using Android Emulator and screen sharing. Our partner accepted what we have implemented. One thing that our partner requested is data security. He wants restricted access to location data for safey reason. Our original decision was omitting authentication except administrator for simplicity. Our partner explained that disclosing locations of field volunteers in wild area could cause dangerous situations such as kidnapping.

