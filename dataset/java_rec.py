import zipfile
import struct
import sys

def detect_java_versions(jar_path):
    versions = []

    with zipfile.ZipFile(jar_path, 'r') as jar:
        for name in jar.namelist():
            if name.endswith(".class"):
                data = jar.read(name)

                # Read first 8 bytes: magic(4), minor(2), major(2)
                magic, minor, major = struct.unpack(">IHH", data[:8])

                if magic != 0xCAFEBABE:
                    continue  # skip invalid class files

                versions.append(major)

    return versions


if __name__ == "__main__":
    jar = sys.argv[1]
    versions = detect_java_versions(jar)

    if not versions:
        print("No class files found.")
    else:
        print("Detected major versions:", sorted(set(versions)))
        print("Highest major:", max(versions))