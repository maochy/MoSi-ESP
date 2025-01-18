# Description of Shanghai Telecom Dataset Preprocessing

To verify the effectiveness of the proposed MoSi-ESP algorithm, we use the publicly available Shanghai Telecom base station dataset to perform comparative experimental analysis. The dataset contains 7.2 million network access records from 9,481 mobile users through 3,233 base stations. Each record includes the start time, end time, latitude and longitude of the base station location. During the experiment, the user access data within 15 days (June 1-15, 2014) in the dataset was selected for empirical study. To adapt the ESP optimization model, we transformed the raw records as follows:

The first step is to group all user access records within 15 days by base station and remove those records that lack base station coordinate information. Secondly, `User Access Time` (UAT), `User Access Num` (UAN) and Base Station `Work Time` (WT) are calculated for each base station. Specifically,

- The user access time $UAT_i$ denotes the total duration of all user access records on the $i$-th base station, measured in minutes (min);
- The number of user accesses $UAN_i$ denotes the total number of times of all user accesses on the $i$-th base station, measured in minutes (min);
- The base station work time $WT_i$ denotes the network access service provided to the user by the $i$-th base station in 15 days in the total time.

*Note: the time of multiple user accesses in the same period is not counted twice.*

Moreover, according to the above variables, the total time of user access ($totalUAT=\sum_{i=1}^{m}{UAT}_i$), the total number of user accesses ($totalUAN=\sum_{i=1}^{m}{UAN}_i$), and the average time of each user visit ($avgUAT$) over a period of 15 days can be further calculated. The specific formula is shown below:

$$avgUAT=\frac{totalUAT}{totalUAN}$$

Subsequently, based on the work time of $i$-th base station (${WT}_i$), the number of user access requests that can be processed in a `serial` manner (${ConNum}_i^{\mathbf{serial}}$) during its work time is calculated by

$${ConNum}_i^{\mathbf{serial}}=\frac{{WT}_i}{AvgUAT}$$

Finally, the average number of user access requests processed in `parallel` by $i$-th base station in 15 days (${ConNum}_i^{\mathbf{parallel}}$) is the ratio of the total number of user accesses (${UAN}_i$) to the number of its user access requests that can be processed serially (${ConNum}_i^{\mathbf{serial}}$). Where, if ${ConNum}_i^{\mathbf{serial}}<1$, the number of user access requests that can be processed in parallel by $i$-th base station in 15 days is 1. The calculation is shown below

$$ConNum_{i}^{\mathbf{parallel}} =
\begin{cases}
1.0, & \text{if } ConNum_{i}^{\mathbf{serial}} < 1 \\
\frac{UAN_{i}}{ConNum_{i}^{\mathbf{serial}}}, & \text{otherwise}
\end{cases}$$

After the above data preprocessing steps, we can get the work time of the $i$-th base station (${WT}_i$) and the average number of user access requests processed in parallel (${ConNum}_i^{\mathbf{parallel}}$), thus the processed base station dataset has the following form:

```text
base station ID=5, latitude=29.263844, longitude=115.02316, worktime=5280 (min), the number of mobile user requests=8.
```

> Notes: More details can be found in the Excel file `Description of the data preprocessing process.xlsx`
