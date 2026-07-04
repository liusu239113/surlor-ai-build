#!/usr/bin/env python3
"""Generate Android app icons from logo.png"""

from PIL import Image
import os

# Icon sizes for different densities
ICON_SIZES = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192,
}

def generate_icons():
    project_root = "D:/WorkBuddy/projects/operit-game-dev"
    logo_path = os.path.join(project_root, "logo.png")
    
    # Open and resize logo
    img = Image.open(logo_path)
    
    for density, size in ICON_SIZES.items():
        # Resize image
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        
        # Save as ic_launcher.png
        output_dir = os.path.join(project_root, "app/src/main/res", density)
        os.makedirs(output_dir, exist_ok=True)
        
        # Regular icon
        regular_path = os.path.join(output_dir, "ic_launcher.png")
        resized.save(regular_path, "PNG")
        print(f"Created: {regular_path}")
        
        # Round icon (same for now, can be customized later)
        round_path = os.path.join(output_dir, "ic_launcher_round.png")
        resized.save(round_path, "PNG")
        print(f"Created: {round_path}")
    
    # Also create a high-res version for Play Store (512x512)
    play_store = img.resize((512, 512), Image.Resampling.LANCZOS)
    play_store_path = os.path.join(project_root, "app/src/main/play_store_icon.png")
    play_store.save(play_store_path, "PNG")
    print(f"Created Play Store icon: {play_store_path}")
    
    print("\nAll icons generated successfully!")

if __name__ == "__main__":
    generate_icons()
