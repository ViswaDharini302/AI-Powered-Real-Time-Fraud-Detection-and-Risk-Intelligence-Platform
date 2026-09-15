import numpy as np
import pandas as pd

np.random.seed(42)
N = 20000  # number of synthetic transactions

rows = []
for i in range(N):
    is_fraud = np.random.rand() < 0.08  # ~8% fraud rate

    if is_fraud:
        amount = np.random.uniform(5000, 100000)
        transaction_frequency = np.random.randint(3, 12)
        is_new_device = np.random.choice([0, 1], p=[0.3, 0.7])
        is_new_location = np.random.choice([0, 1], p=[0.25, 0.75])
        hour = np.random.choice(list(range(0, 5)) + list(range(6, 24)), p=None) if True else 0
        hour = np.random.choice([0, 1, 2, 3, 4, 13, 14, 15], p=[0.18, 0.18, 0.18, 0.14, 0.12, 0.08, 0.06, 0.06])
        amount_deviation = np.random.uniform(4, 20)
    else:
        amount = np.random.uniform(50, 5000)
        transaction_frequency = np.random.randint(0, 4)
        is_new_device = np.random.choice([0, 1], p=[0.9, 0.1])
        is_new_location = np.random.choice([0, 1], p=[0.85, 0.15])
        hour = np.random.randint(6, 23)
        amount_deviation = np.random.uniform(0.2, 3)

    rows.append([amount, transaction_frequency, is_new_device, is_new_location,
                 hour, amount_deviation, int(is_fraud)])

df = pd.DataFrame(rows, columns=[
    "amount", "transaction_frequency", "is_new_device",
    "is_new_location", "hour", "amount_deviation", "is_fraud"
])

df.to_csv("transactions.csv", index=False)
print(f"Wrote {len(df)} rows to transactions.csv")
print(df["is_fraud"].value_counts())