1. All the data you need for Project 1 have been generated in this directory. All the files are tab delimited. You may open them in Excel to have a better view. 
Please note the file structures may be slightly different from what have been listed in the project handout. 
1.1. For some entities, we removed some attributes which are hard to understand and not essential to this project.
1.2. For some entities, we missed some important attributes in the handout. Now we have added them in the files.
The first row of each file describes the file structure, please read it carefully.

2. If a patient has multiple samples, use the average value of those samples as the patient value when you do the OLAP operations unless otherwise specified in the 
project handout.

3. If a sample was tested by multiple experiments, use the average value of those experiments as the sample value unless otherwise specified.

4. If a gene corresponds to multiple microarray probes, use the average value of those probes as the gene value unless otherwise specfied.

5. To save looking up the t-statistic table, we make the following assumptions:
5.1.  In PartIV, 1.3) if the t-statistic value of a gene is greater than or equals to 1.0, this gene is regarded as "informative gene";
5.2.  In PartIV, 2.6) if the t-statistic value on the correlations is greater than or equals to 5.0, the patient is classified as "ALL".

6. In PartIV, 2, you are asked to classify new patients based on the informative genes you find. The microarray data for the new patients are recorded in the file 
"test_samples.txt". The first row lists the names for the patients, while the first column lists the UIDs of the genes. Each of the other cells represents the expression 
value of the corresponding gene in the corresponding patient. You do not need to populate this file into your data wharehouse. Moreover, when you classify the new 
patients, you can read this "test_sample.tst" file directly. But for other data, you have to retrieve them from your data wharehouse.
