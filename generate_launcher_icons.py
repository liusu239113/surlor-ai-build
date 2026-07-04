#!/usr/bin/env python3
"""从项目根目录的 logo.png 生成各密度 Android 启动器图标."""
import os
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).parent
LOGO_PATH = ROOT / "logo.png"
RES_DIR = ROOT / "app" / "src" / "main" / "res"

DENSITIES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}


def generate_icons() -> None:
    if not LOGO_PATH.exists():
        raise FileNotFoundError(f"找不到 logo: {LOGO_PATH}")

    logo = Image.open(LOGO_PATH).convert("RGBA")

    for folder, size in DENSITIES.items():
        out_dir = RES_DIR / folder
        out_dir.mkdir(parents=True, exist_ok=True)

        # 保持原始比例缩放到目标尺寸，使用高质量 LANCZOS 重采样
        resized = logo.resize((size, size), Image.Resampling.LANCZOS)

        square_path = out_dir / "ic_launcher.png"
        resized.save(square_path, "PNG")
        print(f"生成: {square_path}")

        # roundIcon 同样使用方形内容，Android launcher 会自动应用圆形遮罩。
        # 如果 launcher 不裁圆，保持与方形一致也能完整显示 logo。
        round_path = out_dir / "ic_launcher_round.png"
        resized.save(round_path, "PNG")
        print(f"生成: {round_path}")


if __name__ == "__main__":
    generate_icons()
