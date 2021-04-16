# Find Roots

An Android exercise for developers teaching how to play around with intents, activities, services and broadcast receivers
![project diagram](project_diagram.png)

I pledge the highest level of ethical principles in support of academic excellence.
I ensure that all of my work reflects my own abilities and not those of someone else.


### Exercise question answered:
Question:
Testing the CalculateRootsService for good input is pretty easy - we pass in a number and we expect a broadcast intent with the roots.
Testing for big prime numbers can be frustrating - currently the service is hard-coded to run for 20 seconds before giving up, which would make the tests run for too long.
What would you change in the code in order to let the service run for maximum 200ms in tests environments, but continue to run for 20sec max in the real app (production environment)?

Answer:
We could achieve this by adding an argument to the class CalculateRootService which will have 
a default value of 20 seconds, but in the tests we will create it with 200s seconds.
In my implementation, that would mean assigning this argument to MAX_CALC_TIME, which is a constant now, but we can change it into a class member.


### Resources used:
* https://developer.android.com/guide/components/broadcasts
* https://developer.android.com/guide/topics/ui/notifiers/toasts
* https://kotlinlang.org/docs/basic-types.html#string-templates
* https://developer.android.com/training/basics/firstapp/starting-activity