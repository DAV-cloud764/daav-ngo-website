/* ============================================
   DAAV NGO — main.js
   Modules: typewriter, scroll-animations,
            parallax, counter, modal, nav
   ============================================ */

'use strict';

/* =====================
   NAV — scroll behavior + mobile
   ===================== */
const Nav = (() => {
  const nav  = document.querySelector('.nav');
  const ham  = document.querySelector('.nav-hamburger');
  const mob  = document.querySelector('.mobile-nav');
  const cls  = document.querySelector('.mobile-nav-close');
  let open   = false;

  const onScroll = () => {
    nav.classList.toggle('scrolled', window.scrollY > 40);
  };

  const toggleMobile = () => {
    open = !open;
    mob.classList.toggle('active', open);
    document.body.style.overflow = open ? 'hidden' : '';

    if (!open) {
       document.querySelectorAll('.nav-item.dropdown').forEach(item => item.classList.remove('active'));
    }
  };

  const init = () => {
    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll();
    ham?.addEventListener('click', toggleMobile);
    cls?.addEventListener('click', toggleMobile);
    mob?.querySelectorAll('a:not(.dropdown-toggle)').forEach(a => a.addEventListener('click', () => { 
      toggle.addEventListener('click', function (e) {
        if (window.innerWidth <= 768) {
          e.preventDefault();

          const parent = this.parentElement;

          // Close other dropdowns
          document.querySelectorAll('.nav-item.dropdown').forEach(item => {
            if (item !== parent) {
              item.classList.remove('active');
            }
          });

          // Toggle current
          parent.classList.toggle('active');
        }
      });
    });
  };

  return { init };
})();

/* =====================
   TYPEWRITER
   ===================== */
const Typewriter = (() => {
  const phrases = [
    'Improving Lives Through Infection Care in Tanzania',
    'Fighting Malaria, HIV/AIDS & TB in Dar es Salaam',
    'Every Person Deserves Access to Quality Health Care'
  ];

  let phraseIdx = 0, charIdx = 0, deleting = false, paused = false;
  const el = document.getElementById('typewriter-text');

  const TYPING_SPEED  = 55;
  const DELETING_SPEED = 25;
  const PAUSE_END     = 3200;
  const PAUSE_START   = 400;

  const tick = () => {
    if (!el || paused) return;

    const phrase = phrases[phraseIdx];

    if (!deleting) {
      el.textContent = phrase.slice(0, charIdx + 1);
      charIdx++;

      if (charIdx === phrase.length) {
        paused = true;
        setTimeout(() => { paused = false; deleting = true; }, PAUSE_END);
        return;
      }
      setTimeout(tick, TYPING_SPEED);
    } else {
      el.textContent = phrase.slice(0, charIdx - 1);
      charIdx--;

      if (charIdx === 0) {
        paused = true;
        phraseIdx = (phraseIdx + 1) % phrases.length;
        setTimeout(() => { paused = false; deleting = false; tick(); }, PAUSE_START);
        return;
      }
      setTimeout(tick, DELETING_SPEED);
    }
  };

  const init = () => {
    if (!el) return;

    // Use IntersectionObserver to trigger once in viewport
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          observer.disconnect();
          setTimeout(tick, 800);
        }
      });
    }, { threshold: 0.4 });

    observer.observe(el.closest('.hero') || document.body);
  };

  return { init };
})();

/* =====================
   SCROLL ANIMATIONS (IntersectionObserver)
   ===================== */
const ScrollAnimations = (() => {
  const init = () => {
    const targets = document.querySelectorAll('.reveal');

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible');
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });

    targets.forEach(el => observer.observe(el));
  };

  return { init };
})();

/* =====================
   PARALLAX
   ===================== */
const Parallax = (() => {
  const isMobile = () => window.innerWidth <= 768;

  const init = () => {
    const heroBg = document.querySelector('.hero-bg');
    if (!heroBg) return;

    let ticking = false;

    const onScroll = () => {
      if (isMobile()) { heroBg.style.transform = ''; return; }
      if (!ticking) {
        requestAnimationFrame(() => {
          const scrollY = window.scrollY;
          const speed   = 0.3;
          heroBg.style.transform = `translateY(${scrollY * speed}px)`;
          ticking = false;
        });
        ticking = true;
      }
    };

    window.addEventListener('scroll', onScroll, { passive: true });
  };

  return { init };
})();

/* =====================
   COUNTER ANIMATION
   ===================== */
const Counter = (() => {
  const easeOutExpo = t => t === 1 ? 1 : 1 - Math.pow(2, -10 * t);

  const animateCounter = (el, target, duration = 2000, suffix = '') => {
    const start     = performance.now();
    const isDecimal = target % 1 !== 0;

    const step = (now) => {
      const elapsed  = now - start;
      const progress = Math.min(elapsed / duration, 1);
      const eased    = easeOutExpo(progress);
      const current  = target * eased;

      el.textContent = isDecimal
        ? current.toFixed(2)
        : Math.round(current).toLocaleString();

      if (progress < 1) requestAnimationFrame(step);
    };

    requestAnimationFrame(step);
  };

  const init = () => {
    const counters = document.querySelectorAll('.counter');

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const el     = entry.target;
          const target = parseFloat(el.dataset.target);
          const dur    = parseInt(el.dataset.duration) || 2000;
          animateCounter(el, target, dur);
          observer.unobserve(el);
        }
      });
    }, { threshold: 0.5 });

    counters.forEach(el => observer.observe(el));

    // Cost bars
    const bars = document.querySelectorAll('.cost-bar-fill');
    const barObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('animated');
          barObserver.unobserve(entry.target);
        }
      });
    }, { threshold: 0.4 });

    bars.forEach(b => barObserver.observe(b));
  };

  return { init };
})();

/* =====================
   DONATION MODAL
   ===================== */
const DonationModal = (() => {
  const overlay    = document.getElementById('donateModal');
  const triggers   = document.querySelectorAll('[data-modal="donate"]');
  const closeBtn   = document.querySelector('.modal-close');
  const amountBtns = document.querySelectorAll('.amount-btn');
  const customInput = document.getElementById('customAmount');

  const open  = () => { overlay.classList.add('active'); document.body.style.overflow = 'hidden'; };
  const close = () => { overlay.classList.remove('active'); document.body.style.overflow = ''; };

  const init = () => {
    if (!overlay) return;

    triggers.forEach(t => t.addEventListener('click', e => { e.preventDefault(); open(); }));
    closeBtn?.addEventListener('click', close);
    overlay.addEventListener('click', e => { if (e.target === overlay) close(); });

    document.addEventListener('keydown', e => { if (e.key === 'Escape') close(); });

    amountBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        amountBtns.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        if (customInput) customInput.value = '';
      });
    });

    customInput?.addEventListener('focus', () => {
      amountBtns.forEach(b => b.classList.remove('active'));
    });
  };

  return { init };
})();

/* =====================
   DARK MODE TOGGLE
   ===================== */
const DarkMode = (() => {
  const toggle = document.getElementById('darkToggle');
  const html   = document.documentElement;

  const apply = (dark) => {
    html.setAttribute('data-theme', dark ? 'dark' : 'light');
    if (toggle) toggle.textContent = dark ? '☀️' : '🌙';
    localStorage.setItem('daav-theme', dark ? 'dark' : 'light');
  };

  const init = () => {
    const saved = localStorage.getItem('daav-theme');
    const prefers = window.matchMedia('(prefers-color-scheme: dark)').matches;
    apply(saved ? saved === 'dark' : prefers);

    toggle?.addEventListener('click', () => {
      apply(html.getAttribute('data-theme') !== 'dark');
    });
  };

  return { init };
})();

/* =====================
   LANGUAGE TOGGLE (EN / SW)
   ===================== */
const LangToggle = (() => {
  const btn = document.getElementById('langToggle');
  let lang  = 'en';

  const translations = {
    en: {
      'nav-home':      'Home',
      'nav-about':     'About',
      'nav-programs':  'Programs',
      'nav-impact':    'Impact',
      'nav-contact':   'Contact',
      'hero-eyebrow':  'Dar es Salaam · Est. 2019',
      'cta-donate':    'Donate Now',
      'cta-involved':  'Get Involved',
      'stat-lives':    'Lives Impacted',
      'stat-cost':     'Avg. Cost/Intervention',
      'stat-recovery': 'Recovery Rate',
    },
    sw: {
      'nav-home':      'Nyumbani',
      'nav-about':     'Kuhusu',
      'nav-programs':  'Programu',
      'nav-impact':    'Athari',
      'nav-contact':   'Mawasiliano',
      'hero-eyebrow':  'Dar es Salaam · Est. 2019',
      'cta-donate':    'Changia Sasa',
      'cta-involved':  'Jiunge Nasi',
      'stat-lives':    'Maisha Yaliyoathiriwa',
      'stat-cost':     'Gharama / Uingiliaji',
      'stat-recovery': 'Kiwango cha Kupona',
    }
  };

  const apply = () => {
    const t = translations[lang];
    Object.entries(t).forEach(([key, val]) => {
      document.querySelectorAll(`[data-i18n="${key}"]`).forEach(el => {
        el.textContent = val;
      });
    });
    if (btn) btn.textContent = lang === 'en' ? 'SW' : 'EN';
  };

  const init = () => {
    btn?.addEventListener('click', () => {
      lang = lang === 'en' ? 'sw' : 'en';
      apply();
    });
    apply();
  };

  return { init };
})();

/* =====================
   SMOOTH SCROLL for in-page links
   ===================== */
const SmoothScroll = (() => {
  const init = () => {
    document.querySelectorAll('a[href^="#"]').forEach(a => {
      a.addEventListener('click', e => {
        const target = document.querySelector(a.getAttribute('href'));
        if (target) {
          e.preventDefault();
          target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      });
    });
  };

  return { init };
})();

/* =====================
   BOOT
   ===================== */
document.addEventListener('DOMContentLoaded', () => {
  Nav.init();
  Typewriter.init();
  ScrollAnimations.init();
  Parallax.init();
  Counter.init();
  DonationModal.init();
  DarkMode.init();
  LangToggle.init();
  SmoothScroll.init();
});