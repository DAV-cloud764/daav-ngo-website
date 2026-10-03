/* ============================================
   DAAV NGO — programs.js
   Scroll animations, counters, nav for /programs/
   ============================================ */
'use strict';

/* Nav scroll */
const nav = document.querySelector('.nav');
window.addEventListener('scroll', () => {
  nav?.classList.toggle('scrolled', window.scrollY > 40);
}, { passive: true });
nav?.classList.toggle('scrolled', window.scrollY > 40);

/* Mobile nav */
const ham = document.querySelector('.nav-hamburger');
const mob = document.querySelector('.mobile-nav');
const cls = document.querySelector('.mobile-nav-close');
let mOpen = false;
const toggleMob = () => {
  mOpen = !mOpen;
  mob?.classList.toggle('active', mOpen);
  document.body.style.overflow = mOpen ? 'hidden' : '';
};
ham?.addEventListener('click', toggleMob);
cls?.addEventListener('click', toggleMob);
mob?.querySelectorAll('a').forEach(a => a.addEventListener('click', () => { if(mOpen) toggleMob(); }));

/* Scroll reveal */
const revealObs = new IntersectionObserver((entries) => {
  entries.forEach(e => {
    if (e.isIntersecting) { e.target.classList.add('visible'); revealObs.unobserve(e.target); }
  });
}, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });
document.querySelectorAll('.reveal').forEach(el => revealObs.observe(el));

/* Sequential line reveal */
document.querySelectorAll('.seq-reveal').forEach(wrap => {
  const lines = wrap.querySelectorAll('.seq-line');
  const obs = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        lines.forEach((ln, i) => {
          setTimeout(() => ln.classList.add('visible'), i * 130);
        });
        obs.unobserve(e.target);
      }
    });
  }, { threshold: 0.25 });
  obs.observe(wrap);
});

/* Counters */
const easeOut = t => t === 1 ? 1 : 1 - Math.pow(2, -10 * t);
const runCounter = (el, target, dur = 2200) => {
  const start = performance.now();
  const isFloat = target % 1 !== 0;
  const step = now => {
    const p = Math.min((now - start) / dur, 1);
    const v = target * easeOut(p);
    el.textContent = isFloat ? v.toFixed(1) : Math.round(v).toLocaleString();
    if (p < 1) requestAnimationFrame(step);
  };
  requestAnimationFrame(step);
};

const counterObs = new IntersectionObserver((entries) => {
  entries.forEach(e => {
    if (e.isIntersecting) {
      runCounter(e.target, parseFloat(e.target.dataset.target), parseInt(e.target.dataset.dur) || 2200);
      counterObs.unobserve(e.target);
    }
  });
}, { threshold: 0.5 });
document.querySelectorAll('.counter').forEach(el => counterObs.observe(el));

/* Bar chart animations */
const barObs = new IntersectionObserver((entries) => {
  entries.forEach(e => {
    if (e.isIntersecting) { e.target.classList.add('animated'); barObs.unobserve(e.target); }
  });
}, { threshold: 0.3 });
document.querySelectorAll('.bar-row-fill, .cost-bar-fill').forEach(b => barObs.observe(b));

/* Parallax hero */
const heroBg = document.querySelector('.prog-hero-bg');
if (heroBg && window.innerWidth > 768) {
  window.addEventListener('scroll', () => {
    heroBg.style.transform = `translateY(${window.scrollY * 0.28}px)`;
  }, { passive: true });
}

/* Dark mode */
const darkBtn = document.getElementById('darkToggle');
const html = document.documentElement;
const themeIcons = {
  sun: '<svg class="ui-icon icon-sun" viewBox="0 0 24 24" aria-hidden="true" focusable="false"><circle cx="12" cy="12" r="4" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M12 2v2m0 16v2M4.9 4.9l1.4 1.4m11.4 11.4 1.4 1.4M2 12h2m16 0h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>',
  moon: '<svg class="ui-icon icon-moon" viewBox="0 0 24 24" aria-hidden="true" focusable="false"><path d="M20.5 15.2A8.5 8.5 0 0 1 8.8 3.5 8.5 8.5 0 1 0 20.5 15.2Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/></svg>'
};
const applyTheme = d => {
  html.setAttribute('data-theme', d ? 'dark' : 'light');
  if (darkBtn) darkBtn.innerHTML = d ? themeIcons.sun : themeIcons.moon;
  localStorage.setItem('daav-theme', d ? 'dark' : 'light');
};
const saved = localStorage.getItem('daav-theme');
applyTheme(saved ? saved === 'dark' : window.matchMedia('(prefers-color-scheme: dark)').matches);
darkBtn?.addEventListener('click', () => applyTheme(html.getAttribute('data-theme') !== 'dark'));

/* Donation modal */
const overlay = document.getElementById('donateModal');
document.querySelectorAll('[data-modal="donate"]').forEach(t =>
  t.addEventListener('click', e => { e.preventDefault(); overlay?.classList.add('active'); document.body.style.overflow='hidden'; })
);
document.querySelector('.modal-close')?.addEventListener('click', () => { overlay?.classList.remove('active'); document.body.style.overflow=''; });
overlay?.addEventListener('click', e => { if (e.target === overlay) { overlay.classList.remove('active'); document.body.style.overflow=''; } });
document.addEventListener('keydown', e => { if (e.key === 'Escape' && overlay?.classList.contains('active')) { overlay.classList.remove('active'); document.body.style.overflow=''; } });
document.querySelectorAll('.amount-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.amount-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
  });
});

/* Smooth scroll */
document.querySelectorAll('a[href^="#"]').forEach(a => {
  a.addEventListener('click', e => {
    const t = document.querySelector(a.getAttribute('href'));
    if (t) { e.preventDefault(); t.scrollIntoView({ behavior: 'smooth', block: 'start' }); }
  });
});