# 🚛 Truck Routes – Navigation Prototype for Tata Motors

**Truck Routes** is an Android app developed during our Summer Internship at **Tata Technologies Ltd.**, created as a **prototype for Tata Motors’ commercial trucks**. This project was built by a team of R&D interns with the goal of proving how custom route planning and smart navigation can work in real-world trucking scenarios.

---

## 🛠️ What This Project Is

This app is a **Proof of Concept** for a future in-truck navigation system. It allows truck drivers to:

- Enter a **start** and **destination** location
- Choose a **route preference** (fastest, shortest, or avoid tolls)
- View the route drawn on a map
- See **key markers** and **turn-by-turn paths**

Although it’s a prototype, it lays the foundation for a fully integrated navigation system in Tata Motors vehicles.

---

## 🚀 Features

- 📍 **Search by location name**: Just type in city names.
- 🛣️ **Custom routing** using Google’s routes API.
- ⚙️ **Route preferences**:
  - **Speed** – the fastest route considering traffic
  - **Mileage** – the shortest route to save fuel
  - **Avoid Tolls** – skips toll roads
- 🗺️ **Interactive Map** using Google Maps SDK for Compose
- 📡 **Real-time location tracking** (with permission)
- 📌 **Markers** for current, start, and end points
- 🔷 **Route path drawn** using polylines

---

## 🧱 How It Works

- The app takes start and destination as strings (like "Pune" or "28.61,77.23")
- These are converted to coordinates using Geocoder
- Based on the selected preference, it sends a request to **Google Routes API**
- The response includes a path (encoded polyline) that we draw on the map
- User’s location is shown with a marker that updates as the user moves

---

## 📦 Tech Stack

| Part              | Tech Used                     |
|-------------------|-------------------------------|
| UI Framework      | Jetpack Compose               |
| Mapping           | Google Maps SDK for Compose   |
| Location          | Fused Location Provider API   |
| Networking        | Retrofit                      |
| API               | Google Routes API             |

---

## 🧪 Real-World Use Case

This app was built as part of our internship at Tata Technologies in Summer 2025.  
The idea was to create a working **concept demo** of a truck-focused navigation system for **Tata Motors**. We worked as a small R&D team, experimenting with:

- Route APIs
- Location services
- Maps SDK
- UI/UX for truck drivers

The project helped validate that custom routing logic and map rendering could be tailored for **commercial logistics and transportation**.




