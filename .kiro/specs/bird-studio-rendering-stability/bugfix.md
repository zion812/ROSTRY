# Bugfix Requirements Document

## Introduction

The Bird Studio rendering system exhibits visible gaps and disconnections between body parts during rotation and morphing operations. The current implementation uses independent part-by-part rendering with different parallax offsets, causing parts to visually separate when the rotation parameter changes from -1.0 (left) to 1.0 (right). This breaks the visual cohesion of the bird model and creates an unprofessional appearance, especially noticeable during interactive rotation in the studio interface.

The bug affects five critical connection zones: neck-to-head, legs-to-body, wings-to-body, tail-to-body, and chest-to-body seams. Each part is rendered with independent parallax calculations (head: 25f, chest: 10f, body: 5f, tail: -20f, legs: -5f), which causes misalignment as these values diverge during rotation.

This bugfix will implement connection geometry (bridging shapes) between parts to maintain visual continuity across all rotation angles and morphing parameters, ensuring the bird appears as a cohesive whole rather than floating disconnected segments.

## Bug Analysis

### Current Behavior (Defect)

1.1 WHEN rotation parameter is non-zero (bird rotated left or right) THEN the system renders visible gaps between neck and body due to independent parallax offsets (head: 25f vs body: 5f)

1.2 WHEN rotation parameter is non-zero THEN the system renders visible gaps between legs and body due to opposing parallax directions (legs: -5f vs body: 5f)

1.3 WHEN rotation parameter is non-zero THEN the system renders visible gaps between wings and body due to different parallax calculations (wings: body + rotation * 8f vs body: 5f)

1.4 WHEN rotation parameter is non-zero THEN the system renders visible gaps between tail and body due to opposing parallax directions (tail: -20f vs body: 5f)

1.5 WHEN rotation parameter is non-zero THEN the system renders visible seams between chest and body due to independent parallax offsets (chest: 10f vs body: 5f) and clipPath boundaries not aligning

1.6 WHEN bodyWidth morph parameter changes THEN the system renders gaps at connection points because connection geometry does not scale proportionally with body dimensions

1.7 WHEN legLength morph parameter changes THEN the system renders gaps between legs and body because leg attachment point (bodyY - 2f * s) does not adjust for leg length variations

1.8 WHEN neckLength morph parameter exists in BirdAppearance THEN the system renders gaps because neck rendering is not implemented and head jumps directly from body position

1.9 WHEN tailLength morph parameter changes THEN the system renders gaps at tail base because tail rotation pivot (x - bodyW * 0.5f, y - bodyH * 0.8f) does not account for length variations

1.10 WHEN stance parameter changes (UPRIGHT, LOW, CROUCHING, etc.) THEN the system renders gaps because stanceYShift affects vertical positioning but connection geometry does not adapt

### Expected Behavior (Correct)

2.1 WHEN rotation parameter is non-zero THEN the system SHALL render seamless connection geometry between neck and body that bridges the parallax offset difference (20f gap)

2.2 WHEN rotation parameter is non-zero THEN the system SHALL render seamless connection geometry between legs and body that bridges the parallax offset difference (10f gap)

2.3 WHEN rotation parameter is non-zero THEN the system SHALL render seamless connection geometry between wings and body that maintains visual continuity despite different parallax calculations

2.4 WHEN rotation parameter is non-zero THEN the system SHALL render seamless connection geometry between tail and body that bridges the parallax offset difference (25f gap)

2.5 WHEN rotation parameter is non-zero THEN the system SHALL render seamless blending between chest and body clipPath regions that eliminates visible seams

2.6 WHEN bodyWidth morph parameter changes THEN the system SHALL scale all connection geometry proportionally to maintain seamless connections across all body width values (0.0 to 1.0)

2.7 WHEN legLength morph parameter changes THEN the system SHALL adjust leg attachment points and hip socket geometry to maintain seamless connection across all leg length values (0.0 to 1.0)

2.8 WHEN neckLength morph parameter exists THEN the system SHALL render a flexible neck segment that connects head to body seamlessly across all neck length values (0.0 to 1.0)

2.9 WHEN tailLength morph parameter changes THEN the system SHALL render tail base connection geometry that maintains seamless attachment across all tail length values (0.5 to 1.5)

2.10 WHEN stance parameter changes THEN the system SHALL adjust all connection geometry vertical positions to maintain seamless connections across all stance types (UPRIGHT, LOW, GAME_READY, CROUCHING, DISPLAY, NORMAL)

### Unchanged Behavior (Regression Prevention)

3.1 WHEN rotation parameter is zero (bird facing forward) THEN the system SHALL CONTINUE TO render all body parts in their current positions without connection geometry artifacts

3.2 WHEN all morph parameters are at default values (0.5 or 1.0) THEN the system SHALL CONTINUE TO render birds with the same visual appearance as before the fix

3.3 WHEN rendering individual parts (body, head, wings, tail, legs, chest, comb, wattle) THEN the system SHALL CONTINUE TO use existing drawing methods (drawBody, drawHead, drawWing, drawTail, drawLegs, drawChest, drawComb, drawWattle) without modification to their internal rendering logic

3.4 WHEN applying feather patterns (LACED, BARRED, SPECKLED, MOTTLED, PENCILED, etc.) THEN the system SHALL CONTINUE TO render patterns using existing drawFeatheredOval and drawIndividualFeather methods

3.5 WHEN rendering with different body sizes (TINY, BANTAM, SMALL, MEDIUM, LARGE, XLARGE) THEN the system SHALL CONTINUE TO scale all parts proportionally using the sizeMultiplier calculation

3.6 WHEN rendering with different wing styles (FOLDED, SPREAD) THEN the system SHALL CONTINUE TO render wings using existing clipPath and feather rendering logic

3.7 WHEN rendering with different tail styles (SHORT, SICKLE, LONG_SICKLE, FAN, SQUIRREL, WHIP, NONE) THEN the system SHALL CONTINUE TO render tails using existing Path-based drawing with rotation transforms

3.8 WHEN rendering with different leg styles (CLEAN, FEATHERED, HEAVILY_FEATHERED, BOOTED, SPURRED) THEN the system SHALL CONTINUE TO render leg feathering and decorations using existing overlay logic

3.9 WHEN rendering with custom colors (customPrimaryColor, customSecondaryColor, customAccentColor) THEN the system SHALL CONTINUE TO use the resolveColor helper to override default colors

3.10 WHEN rendering with animation parameters (animTime, bobOffset) THEN the system SHALL CONTINUE TO apply breathing animation, head bobbing, and eye blinking effects

3.11 WHEN rendering with selection state (isSelected = true) THEN the system SHALL CONTINUE TO render the golden selection glow and circle outline

3.12 WHEN rendering shadows THEN the system SHALL CONTINUE TO render the ground shadow oval with stance-dependent width adjustments
