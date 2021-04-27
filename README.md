# new_dialog_architecture
POC for Dialog Builder DSL. 

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

````kotlin
MainFragment {
....


     dialog(DialogView<ThisDialogsEvent, SomeState>) {
                    dialogTitle = "Simple Checkbox Dialog"
                    initialState = SomeState()
                    positiveButtonText = "Positive"
                    onPositiveAction = { currentState: SomeState
                        // needs to return a EVENT, can be used to wrap state with event to update fragment with dialogstate
                        ThisDialogsEvent(currentState)
                    }
                }

````


How to create a BottomSheet:
````kotlin
MainFragment {
....

      bottomSheet(DialogView<ThisDialogsEvent, SomeState>) {
                    dialogTitle = "Simple Checkbox Dialog"
                    initialState = SomeState()
                    positiveButtonText = "Positive"
                    onPositiveAction = { currentState: SomeState
                        // needs to return a EVENT, can be used to wrap state with event to update fragment with dialogstate
                        ThisDialogsEvent(currentState)
                    }
                }

````
