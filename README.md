# POC Dialog Builder DSL

this is not final. please take a look try it out and tell me what you think. 
the goal is to reduce the amount of time and code spent on creating dialogs so if it is too complex to use I went the wrong way 😢

quick summary 🤔 

Goal:
State needs to be consistent on Rotation or putting app in background

Interaction between Fragment and DialogFragment needs to consistant with rotation or puttin app in background
avoid repeated Boilerplate code for Dialog creation.

It handles states (via Viewmodel) and interaction(via Interactor) with Fragment (where the dialog was called)
Dialog / BottomSheets have a DialogScaffold which includes Headline and postiive negative Buttons.
For the custom content the dialogBuilder needs/ accepts a class which implements DialogView. This DialogViewImplementation is responsible for the View part.

Interactor:

a Viewmodel (for now, lets see) which holds the eventstream for Events wrapped in a DialogInteractionEvent. Positive / Negative. Is initialized by the parentFragment, childFragment (the dialog) gets it via childFragmentManager an Types.
Child only calls Interactor.send() (for now) if positiveAction is called.

ViewModel:

standard viewmodel for holding state with initial state provided by builder.

DialogView:

interface for customView creation. Is used to create a StateDialog. has current state, and can update state.


DialogBuilder:

builder entity which is responsible for Dialog creation. Minimal arguments are DialogView and initialState.



![Bildschirmfoto 2021-04-27 um 11 30 28](https://user-images.githubusercontent.com/15572029/116221712-28479680-a74e-11eb-824f-a076c79c3e75.png)



How to create a Dialog:

1: create CustomDialogView: for example a checkbox view:
````kotlin
class CheckBoxDialogView : DialogView<ThisInteractorEvent, MultiCheckboxState> {
    override val layoutId: Int = R.layout.some_dialog_content
    override fun setView(view: View, state: () -> MultiCheckboxState, update: (MultiCheckboxState) -> Unit) {
        val list: LinearLayout = view.findViewById(R.id.checkbox_list)
        state().possible.forEach {
            val checkbox = CheckBox(list.context)
            checkbox.text = it
            checkbox.isChecked = state().selected.contains(it)
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                val newList = if (isChecked) state().selected + listOf(it) else state().selected - listOf(it)
                update(state().copy(selected = newList))
            }
            list.addView(checkbox)
        }
    }

}
````

2: in the MainFragment call `dialog(CheckBoxDialogView){}` to create the Dialog.

````kotlin
MainFragment {
....

     dialog(DialogView<ThisInteractorEvent, SomeState>) {
                    dialogTitle = "Simple Checkbox Dialog"
                    initialState = SomeState()
                    positiveButtonText = "Positive"
                    onPositiveAction = { currentState: SomeState
                        // needs to return a EVENT, can be used to wrap state with event to update fragment with dialogstate
                        ThisInteractorEvent(currentState)
                    }
                }

````

or `bottomSheet(CheckBoxDialogView)` to create a bottomSheet with the same checkbox UI

````kotlin
MainFragment {
....

      bottomSheet(DialogView<ThisInteractorEvent, SomeState>) {
                    dialogTitle = "Simple Checkbox Dialog"
                    initialState = SomeState()
                    positiveButtonText = "Positive"
                    onPositiveAction = { currentState: SomeState
                        // needs to return a EVENT, can be used to wrap state with event to update fragment with dialogstate
                        ThisInteractorEvent(currentState)
                    }
                }

````


3. handle dialogevents on MainFragment:

```kotlin
private suspend fun collectDialogEvents() {
        interactor.eventStream(
                onPositive = {
                    when (this) {
                        is MultiCheckboxState -> {
                            buttonDialogCheckbox.text = selected.toString()
                        }
                    }
                },
                onNegative = {}
        ).flowOn(Dispatchers.Main).collect()
    }
```



https://user-images.githubusercontent.com/15572029/116224805-5e3a4a00-a751-11eb-8471-df4ec766df86.mp4



