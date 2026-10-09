package com.course.todoweb.controller;

import com.course.todoweb.model.Priority;
import com.course.todoweb.model.Todo;
import com.course.todoweb.service.TodoService;
import com.course.todoweb.web.TodoForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 * TodoController - a classic Spring MVC @Controller (NOT @RestController).
 * -------------------------------------------------------------------------
 * The methods return a VIEW NAME (a String), e.g. "todos/list".
 * Thymeleaf then renders  src/main/resources/templates/todos/list.html
 * using the data we put into the Model.
 *
 *   "todos/list"          -> render templates/todos/list.html
 *   "redirect:/todos"     -> send HTTP 302, the browser then does GET /todos
 *
 * URL map:
 *   GET  /                    -> redirect to /todos
 *   GET  /todos?filter=...    -> list page (all | active | done)
 *   GET  /todos/new           -> empty form
 *   POST /todos               -> create (validate, then POST-Redirect-GET)
 *   GET  /todos/{id}/edit     -> pre-filled form
 *   POST /todos/{id}          -> update
 *   POST /todos/{id}/toggle   -> mark done / not done
 *   POST /todos/{id}/delete   -> delete
 *
 * HTML forms can only send GET and POST, which is why update/delete use POST.
 */
@Controller
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * A @ModelAttribute METHOD runs before EVERY handler in this controller and
     * adds its result to the Model. The form's dropdown needs the priorities on
     * every form page (new, edit, and when re-showing a form with errors).
     */
    @ModelAttribute("priorities")
    public Priority[] priorities() {
        return Priority.values();
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/todos";
    }

    @GetMapping("/todos")
    public String list(@RequestParam(defaultValue = "all") String filter, Model model) {
        model.addAttribute("todos", todoService.findAll(filter));   // ${todos} in the template
        model.addAttribute("filter", filter);
        model.addAttribute("openCount", todoService.countOpen());
        return "todos/list";                                       // templates/todos/list.html
    }

    @GetMapping("/todos/new")
    public String newForm(Model model) {
        model.addAttribute("todoForm", new TodoForm());   // empty object for th:object
        model.addAttribute("editMode", false);
        return "todos/form";
    }

    @PostMapping("/todos")
    public String create(@Valid @ModelAttribute("todoForm") TodoForm form,
                         BindingResult bindingResult,          // MUST come right after the @Valid object
                         Model model,
                         RedirectAttributes redirectAttributes) {
        // A business rule that needs data -> checked here and added as a field error
        if (form.getTitle() != null && !form.getTitle().isBlank()
                && todoService.titleExists(form.getTitle(), null)) {
            bindingResult.rejectValue("title", "duplicate", "You already have a todo with this title");
        }
        if (bindingResult.hasErrors()) {
            // Show the SAME form again. The form object (with the user's input) and
            // the errors are still in the model, so nothing typed is lost.
            model.addAttribute("editMode", false);
            return "todos/form";
        }
        Todo created = todoService.create(form);
        // Flash attribute: survives exactly ONE redirect, then disappears.
        redirectAttributes.addFlashAttribute("successMessage", "Todo '" + created.getTitle() + "' was added.");
        return "redirect:/todos";   // POST-Redirect-GET: a browser refresh will not submit again
    }

    @GetMapping("/todos/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("todoForm", todoService.toForm(todoService.findById(id)));
        model.addAttribute("editMode", true);
        return "todos/form";
    }

    @PostMapping("/todos/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("todoForm") TodoForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (form.getTitle() != null && !form.getTitle().isBlank()
                && todoService.titleExists(form.getTitle(), id)) {
            bindingResult.rejectValue("title", "duplicate", "You already have a todo with this title");
        }
        if (bindingResult.hasErrors()) {
            form.setId(id);
            model.addAttribute("editMode", true);
            return "todos/form";
        }
        todoService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Todo updated.");
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         @RequestParam(defaultValue = "all") String filter,
                         RedirectAttributes redirectAttributes) {
        todoService.toggle(id);
        // addAttribute (not flash) becomes a query parameter: /todos?filter=active
        redirectAttributes.addAttribute("filter", filter);
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Todo deleted.");
        return "redirect:/todos";
    }
}
