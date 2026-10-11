"""Isolated local smoke test using the existing workspace NeoForge installation."""
from pathlib import Path
import subprocess, shutil, threading, queue, time

root=Path(__file__).resolve().parent
reference=root.parent/'modpack'/'test-server'
java=root.parent/'modpack'/'.tools'/'jdk-21.0.12.1+1'/'bin'/'java.exe'
run=root/'build'/'smoke-server'
run.mkdir(parents=True,exist_ok=True)
(run/'mods').mkdir(exist_ok=True)
for name in ('ftb-quests-neoforge-2101.1.36.jar','ftb-library-neoforge-2101.1.37.jar','ftb-teams-neoforge-2101.1.11.jar','architectury-13.0.11-neoforge.jar'):
    shutil.copy2(root.parent/'modpack'/'.cache'/'mods'/name,run/'mods'/name)
shutil.copy2(root/'build'/'libs'/'gridbound-1.0.0.jar',run/'mods'/'gridbound-1.0.0.jar')
# Reuse the existing test installation's EULA setting; never invent acceptance.
shutil.copy2(reference/'eula.txt',run/'eula.txt')
args=(reference/'libraries'/'net'/'neoforged'/'neoforge'/'21.1.259'/'win_args.txt').read_text()
args=args.replace('libraries/',reference.as_posix()+'/libraries/').replace('-DlibraryDirectory=libraries','-DlibraryDirectory='+reference.as_posix()+'/libraries')
(run/'args.txt').write_text(args)
(run/'server.properties').write_text('server-ip=127.0.0.1\nserver-port=25579\nonline-mode=false\nlevel-name=gridbound-smoke\nlevel-type=minecraft:flat\ngenerator-settings={"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}\ngenerate-structures=false\nview-distance=2\nsimulation-distance=2\nmax-players=1\n')
p=subprocess.Popen([str(java),'-Djdk.net.unixdomain.tmpdir=C:/nonexistent-gridbound-sockets','-Djava.net.preferIPv4Stack=true','-Xmx2G','@args.txt','nogui'],cwd=run,stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True,encoding='utf-8',errors='replace')
q=queue.Queue()
def read():
    for line in p.stdout:q.put(line)
threading.Thread(target=read,daemon=True).start()
deadline=time.monotonic()+180
started=False
with (run/'smoke.log').open('w',encoding='utf-8') as log:
    while time.monotonic()<deadline:
        try:line=q.get(timeout=1)
        except queue.Empty:
            if p.poll() is not None:break
            continue
        log.write(line);log.flush()
        if 'Done (' in line:
            started=True
            p.stdin.write('stop\n');p.stdin.flush()
        if 'ERROR' in line or 'Done (' in line:print(line.strip(),flush=True)
    if p.poll() is None:p.terminate()
    p.wait(timeout=30)
print(f'Started={started}, exit={p.returncode}',flush=True)
raise SystemExit(0 if started and p.returncode==0 else 1)
