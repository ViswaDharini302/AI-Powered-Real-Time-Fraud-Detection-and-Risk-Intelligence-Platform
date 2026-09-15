import pandas as pd
import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import (
    classification_report, confusion_matrix, roc_auc_score
)

FEATURE_COLUMNS = [
    "amount",
    "transaction_frequency",
    "is_new_device",
    "is_new_location",
    "hour",
    "amount_deviation",
]
TARGET_COLUMN = "is_fraud"

def main():
    print("Loading dataset...")
    df = pd.read_csv("transactions.csv")

    X = df[FEATURE_COLUMNS]
    y = df[TARGET_COLUMN]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )

    print("Scaling features...")
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)

    print("Training RandomForestClassifier...")
    model = RandomForestClassifier(
        n_estimators=200,
        max_depth=8,
        class_weight="balanced",  # fraud is rare, so weight it more heavily
        random_state=42,
        n_jobs=-1,
    )
    model.fit(X_train_scaled, y_train)

    print("\n--- Evaluation on held-out test set ---")
    y_pred = model.predict(X_test_scaled)
    y_proba = model.predict_proba(X_test_scaled)[:, 1]

    print(classification_report(y_test, y_pred, digits=3))
    print("Confusion matrix:")
    print(confusion_matrix(y_test, y_pred))
    print(f"ROC-AUC: {roc_auc_score(y_test, y_proba):.4f}")

    print("\nSaving model.pkl and preprocessing.pkl...")
    joblib.dump(model, "model.pkl")
    joblib.dump(scaler, "preprocessing.pkl")
    print("Done. You can now start app.py")

if __name__ == "__main__":
    main()