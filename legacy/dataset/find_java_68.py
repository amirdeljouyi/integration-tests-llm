import zipfile
import struct
import sys

def get_class_major(data: bytes) -> int | None:
    """
    Read the first 8 bytes of a .class file and return the major version.
    Layout: magic (4 bytes), minor (2), major (2)
    """
    if len(data) < 8:
        return None

    magic, minor, major = struct.unpack(">IHH", data[:8])
    if magic != 0xCAFEBABE:
        return None
    return major

def scan_jar_for_major(jar_path: str, target_major: int):
    matches = []

    with zipfile.ZipFile(jar_path, "r") as jar:
        for name in jar.namelist():
            if not name.endswith(".class"):
                continue

            data = jar.read(name)
            major = get_class_major(data)
            if major is None:
                continue

            if major == target_major:
                matches.append((name, major))

    return matches

def main():
    if len(sys.argv) != 2:
        print(f"Usage: python {sys.argv[0]} path/to/jar")
        sys.exit(1)

    jar_path = sys.argv[1]
    target_major = 68  # <-- the one you're interested in

    matches = scan_jar_for_major(jar_path, target_major)

    if not matches:
        print(f"No classes with major version {target_major} found in {jar_path}")
        return

    print(f"Classes with major version {target_major} in {jar_path}:")
    for name, major in matches:
        print(f"{major}\t{name}")

if __name__ == "__main__":
    main()