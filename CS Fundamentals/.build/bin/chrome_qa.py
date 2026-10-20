import sys, os, glob, subprocess, re
root=sys.argv[1]
CH='/Applications/Google Chrome.app/Contents/MacOS/Google Chrome'
INJ='<script>window.__e=[];addEventListener("error",e=>__e.push(String(e.message)));addEventListener("load",()=>setTimeout(()=>{var d=document.createElement("pre");d.id="__qa";d.textContent="QA sw="+document.documentElement.scrollWidth+" errs="+JSON.stringify(__e);document.body.appendChild(d)},800));</script>'
pages=sorted(glob.glob(os.path.join(root,'lessons','0*.html'))+glob.glob(os.path.join(root,'reference','*.html')))
for f in pages:
    d,b=os.path.split(f); tmp=os.path.join(d,'_qa_'+b)
    s=open(f).read(); s=s.replace('<head>','<head>'+INJ,1); open(tmp,'w').write(s)
    try:
        out=subprocess.run([CH,'--headless=new','--disable-gpu','--window-size=420,3000','--virtual-time-budget=4000','--dump-dom','file://'+tmp],capture_output=True,text=True,timeout=60).stdout
    finally: os.remove(tmp)
    m=re.search(r'QA sw=(\d+) errs=(\[.*?\])</pre>',out)
    print(os.path.relpath(f,root), m.group(0)[:-6] if m else 'NO RESULT')
