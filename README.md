**`Emerging Technologies`** course final project. Android application to record how many calories you consume in a day by comparing calorie limits according to your body's calorie needs. With support for nutritional data on various types of food, as well as a simple calorie requirement calculation function using a calculator.

# **Emerging Technologies (B) - `Group 2`**
1. Nabila Kumala Gantari (5026211016)
2. Bayu Siddhi Mukti (5026211021)
3. Vasya Ayu Karmina (5026211091)
4. Maharani Putri Efendi (5026211095)

# Application Description

![alt text](<mockup.png>)

Awareness of the importance of balanced physical health and fitness is increasing in the ever-growing digital era. One of the most important aspects when maintaining health is managing calorie intake. Knowing the number of calories consumed each day can help a person control body weight, maintain energy balance, and prevent health problems such as obesity, diabetes, heart disease and cancer.

The Calorie Diary application is here as a solution to make it easier for users to track what they eat, provide complete nutritional information, calculate users' daily calories accurately, and help achieve health goals such as losing weight or maintaining an ideal body weight.

# **Calories and Nutrition Needs**

Every person's daily calorie needs are different. For this reason, a user's calorie and nutritional needs are calculated using weight, height and age data, which is data collected from users as complementary profile information. Calculations are carried out based on the Basal Metabolic Rate (BMR) calculation formula by Mifflin-St Jeor based on the [following reference](https://www.calculator.net/calorie-calculator.html).

- **[Male]** $BMR = 10 \times weight \space (kg) + 6.25 \times height \space (cm)- 5 \times age + 5$
- **[Female]** $BMR = 10 \times weight \space (kg) + 6.25 \times height \space (cm) - 5 \times age - 161$

Basal Metabolic Rate (BMR) is the number of calories the body needs to perform basic life-sustaining functions. These basic functions include pumping the heart, digesting food, breathing, repairing body cells, and eliminating toxins in the body. BMR is the minimum calorie requirement required by the body when carrying out its basal activities.

However, in implementing the Calorie Diary application, the BMR calculation results are multiplied with a constant of 1.2. This is based on the assumption that users carry out sedentary activities in their daily lives, that is, they remain active with little physical movement. So, the final formula is obtained as follows:

- **[Male]** $calories = 1.2 \times (10 \times weight \space (kg) + 6.25 \times height \space (cm)- 5 \times age + 5)$
- **[Female]** $calories =1.2 \times (10 \times weight \space (kg) + 6.25 \times height \space (cm) - 5 \times age - 161)$

Apart from calculating the user's maximum daily calorie intake, the application will also calculate the user's macronutrient needs which are divided into carbohydrates, protein and fat. The implementation of macronutrient calculations in the Calorie Diary application is based on [other references](https://www.healthline.com/nutrition/how-to-count-macros), where there is a range of ratios of maximum needs for each type of macronutrient to a person's maximum daily calorie intake as follows.

- $macronutrient \space intake \space (cal) = macronutrient \space ratio \space (\%) \times maximum \space calorie \space limit \space (cal)$

In implementing the Calorie Diary, the following macronutrient ratios are used.
- Carbohydrates = 50% total calories
- Protein = 30% total calories
- Fat = 20% total calories

The reference states that each gram of carbohydrate and protein intake is equivalent to 4 calories, while each gram of fat is equivalent to 9 calories. Therefore, calculations are made using a divider of 4 for carbohydrates and protein, and 9 for fat, which will then be calculated in grams. So, the formula for calculating the intake limit for each macronutrient is obtained as follows:

- $carbohydrate \space intake = \large{\frac{0.5 \times maximum \space calorie \space limit \space (cal)}{4(cal/gram)}}$

- $protein \space intake = \large{\frac{0.3 \times maximum \space calorie \space limit \space (cal)}{4 \space (cal/gram)}}$

- $fat \space intake = \large{\frac{0.2 \times maximum \space calorie \space limit \space (cal)}{9 \space (cal/gram)}}$
