// Test loading assets/lesson-runtime.js in JSC
var runtimeScript = read('assets/lesson-runtime.js');
eval(runtimeScript);

if (typeof LessonRuntime === 'undefined') {
  print('FAIL: LessonRuntime is not defined');
} else {
  print('SUCCESS: LessonRuntime loaded cleanly');
  print('Has Whiteboard: ' + (typeof LessonRuntime.Whiteboard.init === 'function'));
  print('Has MultiApproach: ' + (typeof LessonRuntime.MultiApproach.init === 'function'));
  print('Has PracticeArena: ' + (typeof LessonRuntime.PracticeArena.init === 'function'));
  print('Has Quiz: ' + (typeof LessonRuntime.Quiz.init === 'function'));
  print('Has SpacedRecall: ' + (typeof LessonRuntime.SpacedRecall.init === 'function'));
}
