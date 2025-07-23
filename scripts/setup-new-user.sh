#!/bin/bash

# Set your default droplet IP here
DEFAULT_DROPLET_IP="159.203.85.152"

# Check arguments
if [ "$#" -lt 3 ] || [ "$#" -gt 4 ]; then
  echo "Usage: $0 <existing_username> <new_username> <new_password> [droplet_ip]"
  echo "  existing_username: User with sudo access to connect as"
  echo "  new_username: New user to create"
  echo "  new_password: Password for new user"
  echo "  droplet_ip: Optional droplet IP (default: $DEFAULT_DROPLET_IP)"
  exit 1
fi

EXISTING_USER=$1
NEW_USER=$2
NEW_PASS=$3

if [ "$#" -eq 4 ]; then
  DROPLET_IP=$4
else
  DROPLET_IP=$DEFAULT_DROPLET_IP
fi

# You can hardcode the existing user's password here if needed
EXISTING_PASS="your_existing_user_password_here"
# Or uncomment the lines below for interactive input:
echo "Enter password for $EXISTING_USER:"
read -s EXISTING_PASS

echo "👉 Connecting to droplet at $DROPLET_IP as $EXISTING_USER..."

# Create user using existing sudo user
sshpass -p "$EXISTING_PASS" ssh -o StrictHostKeyChecking=no $EXISTING_USER@$DROPLET_IP <<EOF
echo "✅ Creating user '$NEW_USER' with sudo and docker access..."

# Check if user already exists
if id "$NEW_USER" &>/dev/null; then
    echo "⚠️  User '$NEW_USER' already exists. Updating password and groups..."
else
    echo '$EXISTING_PASS' | sudo -S adduser --disabled-password --gecos "" $NEW_USER
fi

# Set password and groups
echo '$EXISTING_PASS' | sudo -S bash -c "echo '$NEW_USER:$NEW_PASS' | chpasswd"
echo '$EXISTING_PASS' | sudo -S usermod -aG sudo,docker $NEW_USER

echo "🔐 Setting up SSH key access for $NEW_USER..."
echo '$EXISTING_PASS' | sudo -S mkdir -p /home/$NEW_USER/.ssh

# Copy SSH keys from existing user (since root login is disabled)
if [ -f ~/.ssh/authorized_keys ]; then
    echo '$EXISTING_PASS' | sudo -S cp ~/.ssh/authorized_keys /home/$NEW_USER/.ssh/
elif [ -f /root/.ssh/authorized_keys ]; then
    # Fallback: try to copy from root if accessible
    echo '$EXISTING_PASS' | sudo -S cp /root/.ssh/authorized_keys /home/$NEW_USER/.ssh/ 2>/dev/null || echo "⚠️  Could not copy SSH keys from root"
else
    echo "⚠️  No SSH keys found to copy. You may need to add SSH keys manually."
fi

echo '$EXISTING_PASS' | sudo -S chown -R $NEW_USER:$NEW_USER /home/$NEW_USER/.ssh
echo '$EXISTING_PASS' | sudo -S chmod 700 /home/$NEW_USER/.ssh
echo '$EXISTING_PASS' | sudo -S chmod 600 /home/$NEW_USER/.ssh/authorized_keys 2>/dev/null

echo "✅ Setup complete for user: $NEW_USER"
EOF

# Test new user SSH access
echo "🔁 Testing SSH login as '$NEW_USER'..."
sshpass -p "$NEW_PASS" ssh -o StrictHostKeyChecking=no $NEW_USER@$DROPLET_IP "echo '🎉 Login successful as $NEW_USER!'"

echo "🎯 User creation completed successfully!"
