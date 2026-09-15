# AI-Powered Real-Time Fraud Detection & Risk Intelligence Platform

AI-Powered Real-Time Fraud Detection & Risk Intelligence Platform is a full-stack application designed to detect potentially fraudulent digital payment transactions and assess their risk in real time. The system combines rule-based fraud detection with a machine learning model to analyze transaction patterns and classify transactions based on their risk level.

## Features

* Real-time fraud detection for digital payment transactions.
* Hybrid fraud detection using rule-based checks and machine learning.
* Machine learning-based fraud prediction using Random Forest.
* Risk classification into Low, Medium, and High.
* Fraud probability and risk assessment for each transaction.
* Detection of suspicious patterns such as unusual transaction amounts, frequent transactions, new devices, and new locations.
* Python-based machine learning microservice using Pandas, NumPy, and Scikit-learn.
* Spring Boot backend for transaction processing and API management.
* REST API communication between the frontend, backend, and ML service.
* MySQL database integration using JPA and Hibernate.
* JWT-based authentication using Spring Security.
* Interactive React.js dashboard for transaction analysis.
* Display of transaction details, risk level, fraud probability, and detected indicators.

## Working

The application follows a hybrid fraud detection workflow that combines rule-based checks with machine learning to assess transaction risk.

1. The user enters the transaction details through the React.js dashboard.
2. The transaction data is sent to the Spring Boot backend through a REST API.
3. The backend validates and processes the transaction information.
4. The transaction is analyzed using features such as transaction amount, transaction frequency, new device, new location, transaction hour, and amount deviation.
5. The transaction data is sent from the Spring Boot backend to the Python machine learning microservice.
6. The Python service preprocesses the input data using Pandas and NumPy.
7. The trained Random Forest model analyzes the transaction and generates a fraud prediction and probability.
8. Rule-based checks are also applied to identify suspicious transaction patterns.
9. The machine learning prediction and rule-based indicators are used to determine the overall transaction risk.
10. The transaction is classified as Low, Medium, or High Risk.
11. The prediction result, fraud probability, and risk indicators are returned to the Spring Boot backend.
12. The React.js dashboard displays the final fraud detection and risk assessment result.
13. Transaction data is stored in MySQL using Spring Data JPA and Hibernate for further analysis.
