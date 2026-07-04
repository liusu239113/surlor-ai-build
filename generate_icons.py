from PIL import Image, ImageDraw
import os

BASE = os.path.dirname(os.path.abspath(__file__))
LOGO = os.path.join(BASE, "logo.png")
RES = os.path.join(BASE, "app", "src", "main", "res")

SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}


def make_round(src: Image.Image, size: int) -> Image.Image:
    """生成圆形图标，保留透明背景。"""
    src = src.resize((size, size), Image.LANCZOS).convert("RGBA")
    mask = Image.new("L", (size, size), 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size, size), fill=255)
    out = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    out.paste(src, (0, 0), mask)
    return out


def main():
    if not os.path.exists(LOGO):
        raise FileNotFoundError(f"找不到 logo: {LOGO}")

    src = Image.open(LOGO).convert("RGBA")

    # 如果 logo 不是正方形，按短边居中裁剪
    w, h = src.size
    if w != h:
        side = min(w, h)
        left = (w - side) // 2
        top = (h - side) // 2
        src = src.crop((left, top, left + side, top + side))

    for folder, size in SIZES.items():
        out_dir = os.path.join(RES, folder)
        os.makedirs(out_dir, exist_ok=True)

        square = src.resize((size, size), Image.LANCZOS)
        square_path = os.path.join(out_dir, "ic_launcher.png")
        square.save(square_path, "PNG")

        round_img = make_round(src, size)
        round_path = os.path.join(out_dir, "ic_launcher_round.png")
        round_img.save(round_path, "PNG")

        print(f"Generated {folder}: {square_path}, {round_path}")


if __name__ == "__main__":
    main()
