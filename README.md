# PetalSuite 🌸

**PetalSuite** is a premium, high-performance management ecosystem tailored for modern florists. Built with a focus on data integrity, real-time monitoring, and a state-of-the-art user experience, it empowers flower shops to scale operations efficiently.

---

## 🚀 Core Features

### 📦 Dynamic Inventory Management
- **Freshness Tracking**: Automated calculation of flower shelf life with real-time freshness percentages.
- **Smart Stock Control**: Intelligent monitoring that identifies low-stock conditions before they impact sales.
- **Supplier Integration**: Seamlessly link inventory to global or local suppliers.

### 💰 Transactional Sales Ecosystem
- **Instant Processing**: Rapid sale recording with automatic stock reconciliation.
- **Transactional Integrity**: Ensures that every sale is atomic—stock updates only happen if the sale is successfully recorded.
- **Customer Awareness**: Track sales by customer name for personalized service.

### ⚠️ Automated Alert Intelligence
- **Severity-Based Alerts**: Distinguishes between critical (`DANGER`) and warning (`WARNING`) states.
- **Dynamic Re-calculation**: Manual and automated sync to ensure alert accuracy.
- **Row-Level Visuals**: Immediate visual feedback within data tables to highlight urgent issues.

### 🛠️ Automated Operations
- **Auto-Backup System**: Scheduled data backups to CSV format every 30 minutes.
- **Background Monitoring**: Continuous health checks for stock and expirations without impacting UI performance.

---

## 🏗️ Technical Architecture

PetalSuite is engineered using a strict **Layered Architecture** (Clean Architecture principles), ensuring scalability and testability:

- **Presentation Layer**: JavaFX-based UI with MVC controllers.
- **Application Layer**: Business orchestration via Service layers and DTO/Validation logic.
- **Domain Layer**: Core business entities and Repository abstractions.
- **Infrastructure Layer**: MySQL persistence, Repository implementations, and connection pooling.

---

## 🛠️ Technology Stack

- **Platform**: Java 17 (OpenJDK)
- **UI Framework**: JavaFX (OpenJFX 17)
- **Database**: MySQL 8.0+
- **Build Tool**: Maven (optional) / Custom PowerShell Automation
- **Design System**: NexaVerse (Custom Vanilla CSS)

---

## 🛠️ Prerequisites

Before you begin, ensure you have the following installed:
- **Java JDK 17** (or higher)
- **MySQL Server 8.0+**
- **Maven** (optional, recommended for first-time dependency resolution)

---

## 🚀 Getting Started (Fastest Way)

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/Y4SSERk/PetalSuite.git
    cd PetalSuite
    ```

2.  **Database Setup**:
    - Ensure your MySQL server is running.
    - Create a database named `florist_db`.
    - *Note: The application will automatically create the tables on first launch.*

3.  **One-Click Launch**:
    - Double-click **`START.bat`** in the root folder.
    - *This will identify missing jars, compile the code, and launch the application!*

---

## 🔧 Developer Commands

If you prefer manual control or want to resolve dependencies first:

```powershell
# Resolve dependencies (Run once if START.bat shows missing jars)
mvn dependency:resolve

# Manual Build & Run
.\build.ps1  # Compile and sync resources
.\run.ps1    # Launch the NexaVerse UI
```

### 2. Database Setup
The application features **Self-Healing Schema Support**. On first launch, it will automatically:
1. Initialize tables from the bundled `schema.sql`.
2. Apply necessary migrations (e.g., severity columns).

> [!NOTE]
> Database connection settings can be adjusted in `com.florist.infrastructure.persistence.DatabaseConnection`.

### 3. Running the Application
The project includes pre-configured automation scripts for Windows:

#### **One-Click Startup (Recommended)**
Double-click `START.bat` in the root folder. This automatically handles the build process (first time) and launches the UI.

#### **Direct Launch (Developer)**
If you have already built the project, you can run directly via PowerShell:
```powershell
.\run.ps1
```

---

## 🎨 Design Philosophy: NexaVerse

PetalSuite features the **NexaVerse** design system—a modern, high-contrast theme focused on usability and aesthetics:
- **Palette**: Deep Crimson and Rose accents on a clean, light backdrop.
- **Typography**: Focused on readability using `Inter` and `Segoe UI`.
- **Interaction**: Premium micro-animations and intuitive hover states (High-visibility text contrast).
