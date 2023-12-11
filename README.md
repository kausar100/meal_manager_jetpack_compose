# meal_manager_jetpack_compose
Meal Manager App serves as a personal helping hand for managing meal and shopping information for mess member.
Build using Kotlin, Jetpack Compose, MVVM, Repository pattern with firebase phone authentication for otp, firestore and storage.

This app has two types of user.
1. Manager
2. Member

Only Manager has access to these feature
1. Add member money to account
2. Add new shopping entry
3. can see all other member info
4. can assign other member as a manager
   
Both type of members have access to following feature
1. see their meal info such as count of meal, meal rate, meal cost
2. add and update meal info
3. see shopping and accound balance information


![406676767_338518042227389_168184257866223757_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/f6013573-581b-4a90-818a-f57f58d7bd5d)
![406524678_1338220860211406_2654851576102235147_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/4dc3b26c-3ff7-4e9c-8e87-6c153ecfca48)
![406501124_323476570536806_1940424652418566800_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/75d12226-8be9-49b9-b35a-5825d3f878d3)
![406495184_2987829744684774_3957995339471123014_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/3a6a0930-6fe1-4cad-8789-099da16cbe9e)
![406482703_1042077640365914_6796881384543141055_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/d184473a-a240-4207-b1a4-256211ecce06)
![406476631_1148262379890125_7882646852663020591_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/de9753c9-e676-4ec2-aa0e-fe329a801a20)
![406446659_720372816667317_7557411954279305196_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/79e2e82b-4d87-4428-9f44-f7c87cef7798)
![406433060_1277206269624590_5620302682791455705_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/563e8954-7553-4da9-a4b4-3b81a5839858)
![406424650_1126168918365851_9086538233331243064_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/f29188a5-b080-49f8-9538-d974be56f341)
![406418453_3737714186457777_6246056745988288655_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/c21d8011-b4ef-4657-8213-eaa655f7a88d)
![406394630_739909201363320_4899730612936315676_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/2774fc46-eab4-4682-8265-1425bd16b5e1)
![406343992_1029973748310955_6208497304701483346_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/7f21a171-f1de-4805-bd0b-fc89cde6145a)
![406343992_862439949011436_5020472571961617901_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/8c28d90a-a8b6-47fe-adc7-b7e6a38c8d55)
![406343991_1460445691185129_5607593169069285125_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/a9493dba-7d49-4be8-98eb-5ddc4ad2676a)
![406334190_905941187717233_2016992575586814542_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/15692299-441a-421e-aac3-a7b3445b1582)
![406281702_1117122999273798_5957284695688525191_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/20b98624-e254-4758-b10f-58a77bc70589)
![406039215_656134496668477_8494594664489571719_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/b33ca6fc-d525-464d-af88-c412fe1a0809)
![405978896_1484601885606706_3585545116637897197_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/8b352378-8b65-4eba-a213-4b655a0da35d)
![404688183_606171261595531_3517572552409785174_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/b1f12209-8add-4c22-8c60-d55000919c9e)
![404459783_1104988870669815_6399931441235038340_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/bfce7b56-c10b-48d8-84be-d37c4e5699f6)
![393065377_1150864625889855_1158259853207473367_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/7bae9837-4782-4753-9400-48cbb9c9693c)
![406511880_730535585791958_3224238149494572919_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/f0a4e7e4-7002-42d7-9e95-b5a25a9afee4)
![406904608_905463250919430_8135919700284638277_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/051358b0-d04b-4ba6-9782-824107d60ea8)
![406780485_638706301786201_2880716052862177986_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/adb891dd-121a-4656-8430-b096dbfe0524)
![406773045_397696209355297_3451278471398392068_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/3b05ba1f-ebd8-4da5-8c44-7e3cff6c527e)
![406682202_1390465331679138_7136048668889216207_n](https://github.com/kausar100/meal_manager_jetpack_compose/assets/55630610/47913d4a-041c-4297-83ff-52e3f1cd2fd0)
