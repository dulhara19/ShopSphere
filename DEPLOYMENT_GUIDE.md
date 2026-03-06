# ShopSphere - Oracle Cloud Deployment Guide

This guide will walk you through deploying ShopSphere (10 microservices + frontend) on an Oracle Cloud Infrastructure (OCI) free-tier VM.

**Time required:** ~45-60 minutes
**Cost:** Free (OCI Always Free tier)

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Create an OCI Account](#2-create-an-oci-account)
3. [Create the VM Instance](#3-create-the-vm-instance)
4. [Connect to Your VM via SSH](#4-connect-to-your-vm-via-ssh)
5. [Install Docker on the VM](#5-install-docker-on-the-vm)
6. [Clone the Project](#6-clone-the-project)
7. [Build and Deploy](#7-build-and-deploy)
8. [Open Firewall Ports](#8-open-firewall-ports)
9. [Verify Deployment](#9-verify-deployment)
10. [Troubleshooting](#10-troubleshooting)
11. [Useful Commands](#11-useful-commands)
12. [Shutting Down / Restarting](#12-shutting-down--restarting)

---

## 1. Prerequisites

Before you start, make sure you have:

- A web browser
- An email address (for OCI account)
- A credit/debit card (OCI requires it for verification, but you will NOT be charged on the free tier)
- An SSH client:
  - **Windows:** Use PowerShell (built-in) or download [PuTTY](https://www.putty.org/)
  - **Mac/Linux:** Terminal (built-in)

---

## 2. Create an OCI Account

If you already have an OCI account, skip to [Step 3](#3-create-the-vm-instance).

1. Go to: https://www.oracle.com/cloud/free/
2. Click **"Start for free"**
3. Fill in your details:
   - Name, email, country
   - Choose your **Home Region** (pick the closest to you, e.g., `ap-mumbai-1` for Sri Lanka/India)
4. Verify your email
5. Set a password
6. Add a payment method (you will NOT be charged)
7. Wait for account provisioning (can take 5-15 minutes)
8. Once ready, sign in at: https://cloud.oracle.com

---

## 3. Create the VM Instance

We'll create an **Always Free ARM VM** with 4 CPUs and 24 GB RAM — more than enough for our project.

### 3.1 Navigate to Compute

1. Sign in to Oracle Cloud Console: https://cloud.oracle.com
2. Click the hamburger menu (top-left, three lines)
3. Go to: **Compute** > **Instances**
4. Click **"Create Instance"**

### 3.2 Configure the Instance

Fill in these settings:

**Name:**
```
shopsphere-server
```

**Placement:** Leave default (your home region)

**Image and Shape:**
1. Click **"Edit"** next to Image and Shape
2. For **Image:** Keep **Oracle Linux 8** (or Ubuntu 22.04 — this guide uses Oracle Linux)
3. For **Shape:** Click **"Change Shape"**
   - Select **Ampere** (ARM-based processor)
   - Shape: **VM.Standard.A1.Flex**
   - Set **OCPUs: 4**
   - Set **Memory: 24 GB**
   - Click **"Select Shape"**

> **Important:** This is within the Always Free tier (up to 4 OCPUs + 24GB on ARM).

**Networking:**
1. Keep default VCN or let it create a new one
2. Make sure **"Assign a public IPv4 address"** is selected (YES)

**SSH Keys:**
1. Select **"Generate a key pair for me"**
2. Click **"Save Private Key"** — this downloads a `.key` file
3. **SAVE THIS FILE SECURELY** — you need it to connect to the VM
4. Also click **"Save Public Key"** as backup

### 3.3 Create

1. Click **"Create"**
2. Wait for the instance status to change from "PROVISIONING" to **"RUNNING"** (1-3 minutes)
3. Once running, note the **Public IP Address** shown on the instance details page

> Example: `129.154.xxx.xxx`

---

## 4. Connect to Your VM via SSH

### Windows (PowerShell)

1. Open PowerShell
2. Navigate to where you downloaded the SSH key:
   ```powershell
   cd Downloads
   ```
3. Connect:
   ```powershell
   ssh -i ssh-key-*.key opc@YOUR_PUBLIC_IP
   ```
   Replace `YOUR_PUBLIC_IP` with the IP from Step 3.3.

4. If you get a permissions error on the key file:
   ```powershell
   icacls "ssh-key-*.key" /inheritance:r /grant:r "$($env:USERNAME):(R)"
   ```
   Then try the ssh command again.

5. Type `yes` when asked about the fingerprint.

### Mac/Linux (Terminal)

```bash
chmod 400 ~/Downloads/ssh-key-*.key
ssh -i ~/Downloads/ssh-key-*.key opc@YOUR_PUBLIC_IP
```

### If Using Ubuntu Image Instead of Oracle Linux

Replace `opc` with `ubuntu`:
```bash
ssh -i ssh-key-*.key ubuntu@YOUR_PUBLIC_IP
```

> You should now see a terminal prompt like: `[opc@shopsphere-server ~]$`

---

## 5. Install Docker on the VM

Copy and paste these commands **one block at a time** into your SSH terminal.

### 5.1 Update the System

```bash
sudo yum update -y
```

### 5.2 Install Docker

```bash
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
```

### 5.3 Start Docker and Enable on Boot

```bash
sudo systemctl start docker
sudo systemctl enable docker
```

### 5.4 Add Your User to the Docker Group

This lets you run Docker commands without `sudo`:

```bash
sudo usermod -aG docker opc
```

### 5.5 Apply the Group Change

**Log out and log back in** for the group change to take effect:

```bash
exit
```

Then SSH in again:
```bash
ssh -i ssh-key-*.key opc@YOUR_PUBLIC_IP
```

### 5.6 Verify Docker Works

```bash
docker --version
docker compose version
```

You should see something like:
```
Docker version 24.x.x
Docker Compose version v2.x.x
```

---

## 5b. If You Chose Ubuntu Instead of Oracle Linux

Use these commands instead:

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo systemctl start docker && sudo systemctl enable docker
sudo usermod -aG docker ubuntu
exit
```

Then reconnect and verify.

---

## 6. Clone the Project

### 6.1 Install Git (if not already installed)

```bash
# Oracle Linux
sudo yum install -y git

# Ubuntu
sudo apt install -y git
```

### 6.2 Clone the Repository

```bash
cd ~
git clone https://github.com/dulhara19/ShopSphere.git
cd ShopSphere
git checkout dev
```

### 6.3 Verify You're on the Right Branch

```bash
git branch
```

You should see:
```
* dev
```

---

## 7. Build and Deploy

### 7.1 Build All Services

This will build all 10 Java microservices + the Next.js frontend. It takes **15-30 minutes** on the first run (downloading dependencies). Be patient.

```bash
cd ~/ShopSphere
docker compose build 2>&1 | tee build.log
```

> **Tip:** If a build fails due to network error, just run the command again. Docker caches completed steps, so it won't restart from scratch.

To build a specific service that failed:
```bash
docker compose build payment-service
```

### 7.2 Verify All Images Built

```bash
docker images | grep -E "shopsphere|service|frontend"
```

You should see **11 images** (10 services + 1 frontend).

### 7.3 Start Everything

```bash
docker compose up -d
```

This starts all containers in the background. The infrastructure services (PostgreSQL, MongoDB, Redis, etc.) start first, then the application services wait for them to be healthy.

### 7.4 Watch the Startup

```bash
docker compose logs -f
```

Press `Ctrl+C` to stop watching logs. Services take 1-3 minutes to fully start.

### 7.5 Check All Containers Are Running

```bash
docker compose ps
```

You should see all containers with status **"Up"** or **"Up (healthy)"**. Example:

```
NAME                        STATUS
shopsphere-postgres         Up (healthy)
shopsphere-mongo            Up (healthy)
shopsphere-redis            Up (healthy)
shopsphere-rabbitmq         Up (healthy)
shopsphere-zookeeper        Up
shopsphere-kafka            Up (healthy)
shopsphere-elasticsearch    Up (healthy)
user-service                Up
product-service             Up
inventory-service           Up
order-service               Up
payment-service             Up
shipping-service            Up
review-service              Up
recommendation-service      Up
notification-service        Up
analytics-service           Up
shopsphere-frontend         Up
```

If any container shows "Restarting" or "Exit", check its logs:
```bash
docker compose logs <service-name>
```

---

## 8. Open Firewall Ports

By default, OCI blocks all incoming traffic. You need to open ports so you can access the app from your browser.

### 8.1 Open Ports in OCI Security List

1. Go to Oracle Cloud Console: https://cloud.oracle.com
2. Navigate to: **Networking** > **Virtual Cloud Networks**
3. Click on your VCN (e.g., `vcn-20260306-xxxx`)
4. Click on your **Subnet** (e.g., `subnet-20260306-xxxx`)
5. Click on the **Security List** (e.g., `Default Security List`)
6. Click **"Add Ingress Rules"**
7. Add this rule:

| Field | Value |
|-------|-------|
| Source Type | CIDR |
| Source CIDR | `0.0.0.0/0` |
| IP Protocol | TCP |
| Destination Port Range | `3000` |
| Description | ShopSphere Frontend |

8. Click **"Add Ingress Rules"**

> **Optional:** If you want to access individual services or RabbitMQ management console, add more rules:
> - Port `15672` — RabbitMQ Management UI
> - Port `3001-3010` — Individual microservice endpoints (for testing/demo)

### 8.2 Open Ports in the VM Firewall (iptables)

SSH into your VM and run:

```bash
# Oracle Linux
sudo firewall-cmd --permanent --add-port=3000/tcp
sudo firewall-cmd --reload

# If you also want direct access to services (optional, for demo):
sudo firewall-cmd --permanent --add-port=3001-3010/tcp
sudo firewall-cmd --permanent --add-port=15672/tcp
sudo firewall-cmd --reload
```

For Ubuntu:
```bash
sudo iptables -I INPUT -p tcp --dport 3000 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 3001:3010 -j ACCEPT
sudo netfilter-persistent save
```

---

## 9. Verify Deployment

### 9.1 Test from the VM

```bash
# Test frontend
curl -s http://localhost:3000 | head -5

# Test individual services
curl -s http://localhost:3001/actuator/health   # user-service
curl -s http://localhost:3002/actuator/health   # product-service
curl -s http://localhost:3003/api/actuator/health   # inventory-service
curl -s http://localhost:3004/actuator/health   # order-service
curl -s http://localhost:3005/actuator/health   # payment-service
curl -s http://localhost:3006/actuator/health   # shipping-service
curl -s http://localhost:3007/actuator/health   # review-service
curl -s http://localhost:3008/actuator/health   # recommendation-service
curl -s http://localhost:3009/actuator/health   # notification-service
curl -s http://localhost:3010/actuator/health   # analytics-service
```

### 9.2 Test from Your Browser

Open your browser and go to:

```
http://YOUR_PUBLIC_IP:3000
```

Replace `YOUR_PUBLIC_IP` with your VM's public IP address from Step 3.3.

You should see the ShopSphere homepage!

### 9.3 Test RabbitMQ Management (Optional)

```
http://YOUR_PUBLIC_IP:15672
```

Login: `guest` / `guest`

---

## 10. Troubleshooting

### A container keeps restarting

Check what's wrong:
```bash
docker compose logs <service-name> --tail 50
```

Common issues:
- **"Connection refused" to postgres/mongo/redis:** The infra service isn't ready yet. Wait a minute, it should auto-recover.
- **"Address already in use":** Another process is using the port. Run `sudo lsof -i :PORT` to find it.

### Build fails with network error

Just retry:
```bash
docker compose build <service-name>
```

Docker caches completed steps, so it won't download everything again.

### Out of disk space

The Always Free VM comes with ~47GB. Check usage:
```bash
df -h
```

Clean up unused Docker images:
```bash
docker system prune -a
```

### Cannot access from browser

1. Check the VM firewall: `sudo firewall-cmd --list-ports`
2. Check OCI Security List has the ingress rule for port 3000
3. Check the frontend container is running: `docker compose ps frontend`

### Services can't connect to each other

Check that all infra services are healthy:
```bash
docker compose ps | grep -E "postgres|mongo|redis|rabbit|kafka"
```

All should show "healthy". If not, restart them:
```bash
docker compose restart shopsphere-postgres shopsphere-mongo shopsphere-redis
```

---

## 11. Useful Commands

```bash
# See all running containers
docker compose ps

# View logs for a specific service
docker compose logs user-service --tail 100

# View logs for all services (live)
docker compose logs -f

# Restart a specific service
docker compose restart product-service

# Stop everything
docker compose down

# Stop everything and delete all data (fresh start)
docker compose down -v

# Rebuild a specific service after code changes
docker compose build <service-name>
docker compose up -d <service-name>

# Check resource usage
docker stats

# Check disk usage
docker system df
```

---

## 12. Shutting Down / Restarting

### To stop all services (keeps data):
```bash
cd ~/ShopSphere
docker compose down
```

### To start again later:
```bash
cd ~/ShopSphere
docker compose up -d
```

### To do a fresh deploy (deletes all databases):
```bash
cd ~/ShopSphere
docker compose down -v
docker compose up -d
```

---

## Architecture Overview

```
Browser (http://YOUR_IP:3000)
    |
    v
[Frontend - Next.js :3000]
    |  (proxies API calls to backend services)
    v
[user-service :3001]  [product-service :3002]  [inventory-service :3003]
[order-service :3004] [payment-service :3005]  [shipping-service :3006]
[review-service :3007] [recommendation-service :3008]
[notification-service :3009] [analytics-service :3010]
    |
    v
[PostgreSQL :5432] [MongoDB :27017] [Redis :6379]
[RabbitMQ :5672]   [Kafka :9092]    [Elasticsearch :9200]
```

## Service Ports Reference

| Service | Port | Database |
|---------|------|----------|
| Frontend | 3000 | - |
| User Service | 3001 | PostgreSQL |
| Product Service | 3002 | MongoDB + Elasticsearch |
| Inventory Service | 3003 | PostgreSQL + Redis + Kafka |
| Order Service | 3004 | PostgreSQL + Redis + RabbitMQ |
| Payment Service | 3005 | PostgreSQL |
| Shipping Service | 3006 | MongoDB |
| Review Service | 3007 | MongoDB + Redis |
| Recommendation Service | 3008 | MongoDB + Redis + RabbitMQ |
| Notification Service | 3009 | MongoDB + RabbitMQ |
| Analytics Service | 3010 | PostgreSQL + Redis + RabbitMQ |

---

## Need Help?

If something goes wrong:

1. Check container logs: `docker compose logs <service-name>`
2. Check all containers: `docker compose ps`
3. Check VM resources: `docker stats` and `free -h`
4. Restart everything: `docker compose down && docker compose up -d`
