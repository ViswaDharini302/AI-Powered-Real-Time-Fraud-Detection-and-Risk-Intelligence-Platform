from flask import Flask, request, jsonify
import joblib
import numpy as np
import os

app = Flask(__name__)

FEATURE_ORDER = [
    "amount",
    "transaction_frequency",
    "is_new_device",
    "is_new_location",
    "hour",
    "amount_deviation",
]

MODEL_PATH = "model.pkl"
SCALER_PATH = "preprocessing.pkl"

model = None
scaler = None

if os.path.exists(MODEL_PATH) and os.path.exists(SCALER_PATH):
    model = joblib.load(MODEL_PATH)
    scaler = joblib.load(SCALER_PATH)
    print("Model loaded successfully.")
else:
    print("WARNING: model.pkl / preprocessing.pkl not found.")
    print("Run 'python generate_dataset.py' then 'python train_model.py' first.")


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "model_loaded": model is not None})


@app.route("/predict", methods=["POST"])
def predict():
    if model is None or scaler is None:
        return jsonify({"error": "Model not trained yet. Run train_model.py first."}), 503

    data = request.get_json(force=True)

    try:
        features = [[data[col] for col in FEATURE_ORDER]]
    except KeyError as e:
        return jsonify({"error": f"Missing field: {e}"}), 400

    features_scaled = scaler.transform(features)
    probability = model.predict_proba(features_scaled)[0][1]  # probability of class "1" = fraud

    return jsonify({"fraud_probability": round(float(probability), 4)})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)