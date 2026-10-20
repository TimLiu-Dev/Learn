import sys, re, os, glob
from html.parser import HTMLParser
root = sys.argv[1]
class P(HTMLParser):
    def __init__(s):
        super().__init__(); s.quizzes=[]; s.stack=[]; s.q=None; s.btn=None; s.links=[]; s.ids=[]
    def handle_starttag(s, t, a):
        a=dict(a); c=a.get('class') or ''
        if t=='a' and a.get('href'): s.links.append(a['href'])
        for k in ('href','src'):
            if t in('link','script') and a.get(k): s.links.append(a[k])
        if t=='div' and 'quiz' in c.split():
            s.q={'id':a.get('data-id'),'ans':int(a.get('data-answer','-1')),'opts':[]}; s.quizzes.append(s.q); s.ids.append(a.get('data-id'))
        if t in('div','section') and 'rating' in c.split() and a.get('data-id'): s.ids.append(a.get('data-id'))
        if t=='div' and 'opts' in c.split(): s.optdepth=len(s.stack)
        if getattr(s,'optdepth',None) is not None and t=='button': s.btn={'text':'','code':False,'plain':''}
        if s.btn is not None and t=='code': s.btn['code']=True; s.incode=True
        s.stack.append(t)
    def handle_endtag(s,t):
        if t=='code': s.incode=False
        if t=='button' and s.btn is not None: s.q['opts'].append(s.btn); s.btn=None
        if t=='div' and s.q is not None and s.btn is None and s.stack and s.stack[-1]=='div':
            pass
        if s.stack: s.stack.pop()
        if t=='div' and getattr(s,'optdepth',None)==len(s.stack): s.optdepth=None
    def handle_data(s,d):
        if s.btn is not None:
            s.btn['text']+=d
            if not getattr(s,'incode',False): s.btn['plain']+=d
bad=0
for f in sorted(glob.glob(os.path.join(root,'lessons','*.html'))):
    p=P(); p.feed(open(f).read()); errs=[]
    for q in p.quizzes:
        o=q['opts']
        if not o: continue
        if not (0<=q['ans']<len(o)): errs.append(f"{q['id']}: answer {q['ans']} out of range {len(o)}")
        codes=[b['code'] for b in o]
        if any(codes) and not all(codes): errs.append(f"{q['id']}: mixes code and text")
        if not any(codes):
            wc=[len(b['text'].split()) for b in o]
            if len(set(wc))>1: errs.append(f"{q['id']}: word counts {wc}")
    dup={i for i in p.ids if i and p.ids.count(i)>1}
    if dup: errs.append(f"duplicate ids {dup}")
    for l in p.links:
        if re.match(r'^(https?:|mailto:|#|data:)',l): continue
        path=l.split('#')[0]
        if path and not os.path.exists(os.path.normpath(os.path.join(os.path.dirname(f),path))): errs.append(f"missing link {l}")
    print(os.path.basename(f), f"{len(p.quizzes)} quizzes", 'OK' if not errs else errs)
    bad+=len(errs)
sys.exit(1 if bad else 0)
