# How to Set Up a Minecraft Java Edition Server: A Complete Guide

## Prerequisites & Requirements

Before you begin, make sure you have the following:

| Requirement       | 	Recommendation                                                           |
|-------------------|---------------------------------------------------------------------------|
| RAM	              | Minimum 4GB; 8GB+ recommended for mods or multiple players                |
| CPU	              | Intel i5 / AMD Ryzen 5 or better                                          | 
| Storage	          | At least 2GB free for server files and world data                         |
| Internet          | 	Stable connection with at least 10 Mbps upload/download for 5–10 players |
| Minecraft Account | 	Java Edition account (required for online mode)                          |

## Step 1: Install Java

Minecraft servers run on Java. You'll need Java 17 or newer, depending on your Minecraft version.

### Windows

1. Download Java 17+ from Adoptium 
2. Run the installer
3. (Optional) Add Java to your system PATH:

```cmd
setx PATH "%PATH%;C:\Program Files\Eclipse Adoptium\jdk-17\bin"
```

### Linux (Debian/Ubuntu)

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
```

Verify the installation:

```bash
java -version
```

## Step 2: Download the Server Software

You have two main options:

Option A: Official Vanilla Server — the standard Minecraft experience
* Go to minecraft.net/en-us/download/server 
* Download the latest `server.jar` file

Option B: PaperMC — performance-optimized with plugin support (recommended)
* Download from papermc.io

### Create a Server Folder

Create a dedicated folder for your server files:

#### Windows:

```text
C:\MinecraftServer
```

#### Linux:

```bash
mkdir -p ~/minecraft/server
cd ~/minecraft/server
```

Place the downloaded `.jar` file in this folder. Rename it to `server.jar` for simplicity.

## Step 3: Accept the EULA

Run the server once to generate the necessary files. The first attempt will fail—this is normal.

### Windows:

Double-click the `.jar` file or run from Command Prompt:

```cmd
java -jar server.jar nogui
```

### Linux:

```bash
java -jar server.jar nogui
```

After it fails, you'll see a `eula.txt` file in your server folder. Open it and change:

```text
eula=false
```

to:

```text
eula=true
```

Important: By accepting the EULA, you agree to Mojang's terms.

## Step 4: Create a Startup Script

### Windows - `start.bat`

Right-click in your server folder → New → Text Document. Rename it to start.bat. Edit and paste:

```bat
@echo off
title Minecraft Server
java -Xmx4G -Xms2G -jar server.jar nogui
pause
```

* -Xmx4G: Maximum RAM allocation (adjust to your system)
* -Xms2G: Initial RAM allocation 
* nogui: Runs without graphical interface (saves resources)

Double-click `start.bat` to launch your server.

### Linux - `start.sh`

Create the script:

```bash
#!/bin/bash
cd "$(dirname "$0")"
java -Xmx4G -Xms2G -jar server.jar nogui
```

Make it executable and run it:

```bash
chmod +x start.sh
./start.sh
```

## Step 5: Basic Server Configuration

After running the server once, a server.properties file is generated. Open it with a text editor to customize your server:

| Setting	               | Description                                      | 	Example                   |
|------------------------|--------------------------------------------------|----------------------------| 
| `motd`                 | 	Server welcome message	                         | motd=Welcome to My Server! |
| `max-players`          | 	Maximum concurrent players                      | 	max-players=20            |
| `difficulty`           | peaceful, easy, normal, hard	                    | difficulty=normal          |
| `gamemode`             | survival, creative, adventure, spectator	        | gamemode=survival          |
| `pvp`                  | Enable/disable player combat                     | 	pvp=true                  |
| `online-mode`          | 	true = premium accounts only; false = allow all | 	online-mode=true          |
| `enable-command-block` | 	Allow command blocks	                           | enable-command-block=false |

Important: Setting online-mode=false allows anyone to join, including non-premium accounts, but this increases security risks.

## Step 6: Connect to Your Server

1. Launch Minecraft Java Edition 
2. Click Multiplayer → Add Server 
3. Enter a name and set the server address:
   * If playing on the same machine: localhost or 127.0.0.1 
   * If on the same local network: use your computer's local IP address
4. Click Done and join

## Step 7: Allow Friends to Join (Port Forwarding & Alternatives)

### Method 1: Port Forwarding (Traditional)

Minecraft uses TCP port 25565 by default.

1. Find your local IPv4 address:
   * Windows: Open Command Prompt → ipconfig → Look for "IPv4 Address"
   * Linux: `ip -o route get to 8.8.8.8 | sed -n 's/.*src \([0-9.]\+\).*/\1/p'`
2. Log into your router: Usually at 192.168.1.1 or 192.168.0.1 
3. Forward port 25565:
   * Find the "Port Forwarding" section 
   * Create a rule: Port 25565, Protocol TCP/UDP, Forward to your computer's IPv4 address
4. Allow through firewall: Windows: Create an inbound rule for port 25565 in Windows Defender Firewall
5. Share your public IP with friends (Google "what is my IP")

### Method 2: PlayIt.gg (Easier Alternative)

If port forwarding seems complicated, use PlayIt.gg to create a tunnel:

1. Create an account at playit.gg 
2. Download the PlayIt agent and place it in your server folder 
3. Run PlayIt and create a tunnel → select Minecraft Java 
4. Share the generated IP address with your friends

### Method 3: Tailscale (Private Network)

Use Tailscale to create a secure private network:

1. Install Tailscale on your server and each friend's computer 
2. Connect all devices to your Tailscale network 
3. Share your server's Tailscale IP (starts with 100.x.y.z) with friends 
4. No port forwarding required

## Advanced: Running on Linux with screen or systemd

### Using screen (Keep server running after logout)

Install screen:

```bash
sudo apt install screen
```

Start a screen session and run the server:

```bash
screen -S minecraft
./start.sh
```

Detach from screen: Press CTRL + A, then D

Reattach later:

```bash
screen -r minecraft
```

### Using systemd (Auto-start on boot)

Create a service file at `/etc/systemd/system/minecraft.service`:

```ini
[Unit]
Description=Minecraft Server
After=network.target

[Service]
Type=simple
User=minecraft
WorkingDirectory=/home/minecraft/server
ExecStart=/usr/bin/java -Xmx4G -Xms2G -jar server.jar nogui
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:

```bash
sudo systemctl enable minecraft
sudo systemctl start minecraft
```

## Troubleshooting Common Issues

| Issue	                                                                 | Solution                                                                                      |
|------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| "Java not recognized"	                                                 | Add Java to your system PATH or reinstall Java                                                |
| Server won't start	Check EULA is set to true; verify Java installation |                                                                                               |
| Friends can't connect	                                                 | Confirm port forwarding is correct; check firewall settings; verify online-mode compatibility |
| Out of Memory (OOM)	                                                   | Reduce -Xmx value or upgrade system RAM                                                       |
| Port already in use	                                                   | Close other Minecraft servers or change server-port in server.properties                      |
| Can't connect locally	                                                 | Use localhost as address; ensure server has fully started (look for "Done!" in console)       |

## Final Notes

* Backup regularly: Copy your world folder to a safe location 
* Update Java periodically: Security updates are important 
* Monitor performance: If players experience lag, consider reducing view-distance or using PaperMC

Happy hosting! 🎮