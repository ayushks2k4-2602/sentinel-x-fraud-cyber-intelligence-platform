import urllib.request
import json
import time
import uuid
import random

API_URL = "http://localhost:8080/api/v1/events/financial"

def generate_event(event_index):
    return {
        "eventId": f"EVT-BENCH-{event_index:05d}-{uuid.uuid4().hex[:6]}",
        "eventType": random.choice(["TRANSACTION", "LOGIN", "DEVICE_CHANGE", "BENEFICIARY_ADDED"]),
        "schemaVersion": "1.0",
        "timestamp": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
        "source": "SYNTHETIC_BENCHMARK_GENERATOR",
        "accountId": f"ACC-{random.randint(1000, 9999)}",
        "deviceId": f"DEV-{random.randint(100, 999)}",
        "ipAddress": f"185.220.{random.randint(100, 200)}.{random.randint(1, 250)}",
        "payload": {
            "transactionId": f"TXN-{random.randint(100000, 999999)}",
            "amount": round(random.uniform(500, 150000), 2),
            "currency": "INR",
            "type": "IMPS_TRANSFER",
            "beneficiaryName": "Global Pay Corp",
            "beneficiaryAccount": f"BEN-{random.randint(10000, 99999)}"
        }
    }

def run_benchmark(total_events=1000):
    print(f"Starting SENTINEL-X Synthetic Ingestion Benchmark ({total_events} events)...")
    success_count = 0
    duplicate_test_count = 0
    start_time = time.time()

    for i in range(1, total_events + 1):
        event = generate_event(i)
        data = json.dumps(event).encode('utf-8')
        req = urllib.request.Request(API_URL, data=data, headers={'Content-Type': 'application/json'})

        try:
            with urllib.request.urlopen(req) as response:
                if response.status in (200, 202):
                    success_count += 1
        except Exception as e:
            # Server offline or connection refused in offline mode
            pass

        if i % 250 == 0:
            elapsed = time.time() - start_time
            print(f"Processed {i}/{total_events} events... (Elapsed: {elapsed:.2f}s)")

    total_time = time.time() - start_time
    print(f"\n==========================================")
    print(f"BENCHMARK COMPLETED")
    print(f"Total Events Generated: {total_events}")
    print(f"Accepted Events: {success_count}")
    print(f"Total Time: {total_time:.2f}s")
    if total_time > 0 and success_count > 0:
        print(f"Measured Ingestion Throughput: {success_count / total_time:.2f} EPS (Events/sec)")
    print(f"==========================================\n")

if __name__ == "__main__":
    run_benchmark(1000)
