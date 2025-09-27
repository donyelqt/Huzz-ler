# Rewards Images Assets

This folder is where you can place the actual reward images for your Huzzler app.

## Image Guidelines:
- **Size**: 400x400 pixels or larger (square format preferred)
- **Format**: PNG or JPG
- **Quality**: High resolution for crisp display

## Suggested Image Names:
- `valorant_character.png` - For Valorant Points reward
- `mlbb_character.png` - For MLBB Battle Pass reward
- `academic_badge.png` - For academic rewards
- `game_reward_1.png`, `game_reward_2.png` - For additional game rewards

## How to Use:
1. Place your images in this folder
2. Update the RewardAdapter.kt file to load these images instead of placeholders
3. You can use libraries like Glide or Picasso to load images from assets

## Current Placeholders:
The app currently uses vector drawable placeholders:
- `placeholder_valorant.xml` - Red themed Valorant placeholder
- `placeholder_mlbb.xml` - Purple themed MLBB placeholder

Replace these by loading actual images from this assets folder when ready.
