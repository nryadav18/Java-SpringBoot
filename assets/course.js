/* ==========================================================
   Java + Spring Boot Course - shared script
   - light / dark theme toggle (remembered in this browser)
   - "Copy" button on every code block
   - tiny Java/XML/YAML syntax colouring (works offline, no libraries)
   - "mark topic as done" checkbox on lesson pages
   ========================================================== */
(function () {
  "use strict";

  function store(key, value) {
    try {
      if (value === undefined) return localStorage.getItem(key);
      localStorage.setItem(key, value);
    } catch (e) { return null; }
  }

  // ---------- theme ----------
  var saved = store("course-theme");
  if (saved) document.documentElement.setAttribute("data-theme", saved);

  function currentTheme() {
    var t = document.documentElement.getAttribute("data-theme");
    if (t) return t;
    return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
  }

  document.addEventListener("DOMContentLoaded", function () {
    var btn = document.querySelector(".theme-btn");
    if (btn) {
      var label = function () { btn.textContent = currentTheme() === "dark" ? "☀ Light" : "☾ Dark"; };
      label();
      btn.addEventListener("click", function () {
        var next = currentTheme() === "dark" ? "light" : "dark";
        document.documentElement.setAttribute("data-theme", next);
        store("course-theme", next);
        label();
      });
    }

    // ---------- syntax colouring + copy buttons ----------
    document.querySelectorAll("pre > code").forEach(function (code) {
      var pre = code.parentElement;
      if (!pre.classList.contains("terminal") && !code.classList.contains("no-hl")) highlight(code);
      var b = document.createElement("button");
      b.className = "copy-btn";
      b.type = "button";
      b.textContent = "Copy";
      b.addEventListener("click", function () {
        var text = code.innerText;
        var done = function () { b.textContent = "Copied!"; setTimeout(function () { b.textContent = "Copy"; }, 1400); };
        if (navigator.clipboard) navigator.clipboard.writeText(text).then(done, done); else done();
      });
      pre.appendChild(b);
    });

    // ---------- progress tracking ----------
    // Lesson pages: <input type="checkbox" class="done-check" data-topic="7-OOP-Encapsulation-Inheritance">
    document.querySelectorAll(".done-check").forEach(function (box) {
      var key = "course-done:" + box.getAttribute("data-topic");
      box.checked = store(key) === "1";
      box.addEventListener("change", function () { store(key, box.checked ? "1" : "0"); updateCount(); });
    });
    updateCount();
  });

  function updateCount() {
    var el = document.getElementById("progress-count");
    if (!el) return;
    var boxes = document.querySelectorAll(".done-check");
    var n = 0;
    boxes.forEach(function (b) { if (b.checked) n++; });
    el.textContent = n + " / " + boxes.length + " topics done";
  }

  // ---------- very small highlighter ----------
  var JAVA_KW = "abstract|assert|boolean|break|byte|case|catch|char|class|continue|default|do|double|else|enum|extends|final|finally|float|for|if|implements|import|instanceof|int|interface|long|new|package|private|protected|public|record|return|sealed|permits|short|static|super|switch|synchronized|this|throw|throws|try|var|void|volatile|while|yield|true|false|null";

  function esc(s) { return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); }

  function highlight(code) {
    var lang = (code.className.match(/lang-(\w+)/) || [])[1] || "java";
    var src = code.textContent;
    var rules;
    if (lang === "xml" || lang === "html") {
      rules = [
        ["tok-com", /<!--[\s\S]*?-->/y],
        ["tok-str", /"[^"]*"/y],
        ["tok-kw", /<\/?[\w:.-]+|\/?>/y]
      ];
    } else if (lang === "yaml" || lang === "properties" || lang === "yml") {
      rules = [
        ["tok-com", /#.*/y],
        ["tok-kw", /^[ \t-]*[\w.\-\[\]]+(?=\s*[:=])/my],
        ["tok-str", /"[^"\n]*"|'[^'\n]*'/y],
        ["tok-num", /\b\d+(\.\d+)?\b/y]
      ];
    } else if (lang === "sql") {
      rules = [
        ["tok-com", /--.*/y],
        ["tok-str", /'[^'\n]*'/y],
        ["tok-kw", /\b(SELECT|FROM|WHERE|INSERT|INTO|VALUES|UPDATE|SET|DELETE|CREATE|TABLE|PRIMARY|KEY|FOREIGN|REFERENCES|JOIN|LEFT|INNER|ON|AND|OR|NOT|NULL|ORDER|BY|GROUP|LIMIT|AS|INT|BIGINT|VARCHAR|DECIMAL|AUTO_INCREMENT|DROP|ALTER|ADD|INDEX|UNIQUE|DEFAULT|BOOLEAN|TIMESTAMP|IDENTITY|select|from|where|insert|into|values|update|set|delete|create|table|primary|key|join|on|and|or|order|by|group|as)\b/y],
        ["tok-num", /\b\d+(\.\d+)?\b/y]
      ];
    } else if (lang === "none" || lang === "text" || lang === "bash" || lang === "shell") {
      return;
    } else {
      rules = [
        ["tok-com", /\/\/.*|\/\*[\s\S]*?\*\//y],
        ["tok-str", /"""[\s\S]*?"""|"(?:\\.|[^"\\\n])*"|'(?:\\.|[^'\\\n])'/y],
        ["tok-ann", /@\w+/y],
        ["tok-kw", new RegExp("\\b(?:" + JAVA_KW + ")\\b", "y")],
        ["tok-type", /\b[A-Z][A-Za-z0-9_]*\b/y],
        ["tok-num", /\b\d[\d_]*(\.\d+)?[LlFfDd]?\b/y]
      ];
    }
    var out = "", i = 0, plain = "";
    outer: while (i < src.length) {
      for (var r = 0; r < rules.length; r++) {
        var re = rules[r][1];
        re.lastIndex = i;
        var m = re.exec(src);
        if (m && m.index === i && m[0].length) {
          out += esc(plain); plain = "";
          out += '<span class="' + rules[r][0] + '">' + esc(m[0]) + "</span>";
          i += m[0].length;
          continue outer;
        }
      }
      // advance one "word" or one char of plain text
      var w = /[A-Za-z0-9_$]+|[\s\S]/y;
      w.lastIndex = i;
      var t = w.exec(src)[0];
      plain += t; i += t.length;
    }
    out += esc(plain);
    code.innerHTML = out;
  }
})();
