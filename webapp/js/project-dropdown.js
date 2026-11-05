
document.addEventListener("DOMContentLoaded", function () {

    const dropdown = document.querySelector(".project-dropdown");

    if (!dropdown) return;

    const megaMenu = dropdown.querySelector(".project-mega-menu");
    const projectButtons = dropdown.querySelectorAll(".project-select-btn");
    const projectPanels = dropdown.querySelectorAll(".project-menu-panel");

    // URL에 projectId가 있으면 해당 프로젝트를 우선 선택
    const params = new URLSearchParams(window.location.search);
    const currentProjectId = params.get("projectId");

    // 프로젝트 선택 및 오른쪽 메뉴 변경
    function selectProject(projectId) {

        projectButtons.forEach(function (button) {
            button.classList.remove("active");
        });

        projectPanels.forEach(function (panel) {
            panel.classList.remove("active");
        });

        const selectedButton = Array.from(projectButtons).find(
            function (button) {
                return button.dataset.projectId === projectId;
            }
        );

        const selectedPanel = Array.from(projectPanels).find(
            function (panel) {
                return panel.dataset.projectPanel === projectId;
            }
        );

        if (selectedButton && selectedPanel) {
            selectedButton.classList.add("active");
            selectedPanel.classList.add("active");
            megaMenu.classList.add("has-selection");
        }
    }

    // 프로젝트 메뉴에 마우스를 올리면 현재 프로젝트 또는 첫 번째 프로젝트 선택
    dropdown.addEventListener("mouseenter", function () {

        if (projectButtons.length === 0) return;

        const projectExists = Array.from(projectButtons).some(
            function (button) {
                return button.dataset.projectId === currentProjectId;
            }
        );

        const projectId = projectExists
            ? currentProjectId
            : projectButtons[0].dataset.projectId;

        selectProject(projectId);
    });

    // 프로젝트를 클릭하면 해당 프로젝트의 메뉴로 변경
    projectButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            selectProject(button.dataset.projectId);
        });
    });

    // 아직 연결되지 않은 기능은 임시 링크로 유지
    dropdown.querySelectorAll('[data-placeholder="true"]')
        .forEach(function (link) {
            link.addEventListener("click", function (event) {
                event.preventDefault();
            });
        });

});
