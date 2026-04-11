document.addEventListener("DOMContentLoaded", () => {
    const flashes = document.querySelectorAll(".flash");
    flashes.forEach((flash) => {
        setTimeout(() => {
            flash.style.transition = "opacity 300ms ease";
            flash.style.opacity = "0";
        }, 2800);
    });

    const toggles = document.querySelectorAll("[data-toggle-target]");
    toggles.forEach((btn) => {
        btn.addEventListener("click", () => {
            const targetId = btn.getAttribute("data-toggle-target");
            if (!targetId) {
                return;
            }

            const target = document.getElementById(targetId);
            if (!target) {
                return;
            }

            target.classList.toggle("open");
        });
    });
});
