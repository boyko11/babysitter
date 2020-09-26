# Babysitter

One Night Payment Calculator Service.

## Run
Assumes Java 8+.  
Import as a maven project.  
Run the unit tests.  

## Use Cases assumptions and comments:

### Starts no earlier than 5:00PM
The PayCalculatorService just calculates pay - it does not enforce clock-in during the allowed period.  
If an earlier then allowed clock-in time is specified, an error message would be returned,  
and the pay returned would be zero. IE, the assumptions is that some other service helps the babysitter  
work only during the allowed period. 
It is also not smart enough to calculate multiple clock-ins and clock-outs for the same work period -  
It calculates a single period clock-in - clock-out pay.  

### Leaves no later than 4:00AM  
Assumes valid start and leave time within a 24 hour period.  
IE, it is not smart enough to let the babysitter know that they would get paid only for one day, even if
they clocked in on Jan. 1st, but clocked out sometime February.  
It's a harsh PayCalculator, but not because it's evil, but rather because it is not very bright.  

### Gets paid for full hours (no fractional hours)
Not much smarts as far as rounding - It's Math.round  
IE, 1.5 will be rounded to 2, 1.499999 will be rounded to 1.  
Rounds for all time periods.   
E.G.  
worked 1.5 hours in the first period  
worked 2.7 hours in the second period  
worked 2.2 hours in the third period  

```Java
payment =  
Math.round(1.5) * first_period_rate + 
Math.round(2.7) * second_period_rate + 
Math.round(2.2) * third_period_rate  
```  

Most assumptions, especially the last one, are things that are clarified through discussions.  
For the sake of less noise, I did not attempt to contact the product owner.  
In reality, I do not assume. I collaborate, discuss and clarify.  

