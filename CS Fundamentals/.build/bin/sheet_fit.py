import sys, os, glob, subprocess, re
root=sys.argv[1]; widths=[int(w) for w in sys.argv[2].split(',')]
CH='/Applications/Google Chrome.app/Contents/MacOS/Google Chrome'
INJ='<script>addEventListener("load",()=>setTimeout(()=>{var bad=[];document.querySelectorAll(".sheet section, main table").forEach(el=>{var t=el.tagName=="TABLE"?el:el.querySelector("table");if(!t)return;var box=el.tagName=="TABLE"?el.parentElement:el;if(t.offsetWidth>box.clientWidth+1)bad.push((el.querySelector("h2")||{}).textContent+":"+t.offsetWidth+">"+box.clientWidth)});var d=document.createElement("pre");d.id="__qa";d.textContent="FIT "+JSON.stringify(bad);document.body.appendChild(d)},500));</script>'
for f in sorted(glob.glob(os.path.join(root,'reference','*.html'))):
    d,b=os.path.split(f); tmp=os.path.join(d,'_qa_'+b)
    open(tmp,'w').write(open(f).read().replace('<head>','<head>'+INJ,1))
    try:
        for w in widths:
            out=subprocess.run([CH,'--headless=new','--disable-gpu','--window-size=%d,3000'%w,'--virtual-time-budget=3000','--dump-dom','file://'+tmp],capture_output=True,text=True,timeout=60).stdout
            m=re.search(r'FIT (\[.*?\])</pre>',out); print(b,w,m.group(1) if m else 'NO RESULT')
    finally: os.remove(tmp)
